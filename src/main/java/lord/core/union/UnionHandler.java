package lord.core.union;

import beengine.Server;
import beengine.network.RawPacketHandler;
import beengine.scheduler.AsyncTask;
import beengine.scheduler.Scheduler;
import beengine.scheduler.TaskControl;
import beengine.util.binary.NioBuffer;
import beengine.util.config.Config;
import lord.core.Lord;
import lord.core.union.packet.*;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

public class UnionHandler implements RawPacketHandler {

	private Map<InetSocketAddress, UnionServer> servers = new HashMap<>();
	private Map<String, UnionServer> serversById = new HashMap<>();
	private UnionConfig config;
	private UnionPacket[] packets = new UnionPacket[64];
	private TaskControl updateTask;
	private UnionDataProvider provider = new UnionDataProvider();
	private IntSupplier onlineCalculator = () -> Server.unsafe().playerList().size();
	private UnionServer thisServer;
	private Predicate<UnionServer> statusSendFilter = server -> server != thisServer;

	public UnionHandler() {
		config = UnionConfig.loadFromDir(Lord.instance.dataPath(), UnionConfig.class);
		if (!Files.exists(config.file())) {
			config.save();
		}
		config.servers.forEach((id, data) -> {
			UnionServer server = new UnionServer(id, data.name, data.ip, data.port);
			addServer(server);
		});
		UnionThisServerID thisIdConfig = Config.loadFromDir(Lord.instance.dataPath(), UnionThisServerID.class);
		if (!Files.exists(thisIdConfig.file())) {
			thisIdConfig.save();
		}
		thisServer = getServer(thisIdConfig.id);
		if (thisServer == null) {
			throw new RuntimeException("This server not found ("+thisIdConfig.id+")");
		}
		thisServer.isOnline = true;

		registerPacket(new UpdateStatus());
		registerPacket(new GamerDataRequest());
		registerPacket(new GamerDataResponse());
		registerPacket(new GamerDataSave());
		registerPacket(new GamerDataSaved());

		updateTask = Scheduler.delayedRepeat(20, config.statusSendFrequency * 20, new Runnable() {
			int sendToOfflineCounter;
			@Override
			public void run() {
				boolean offline;
				if (--sendToOfflineCounter < 0) {
					offline = true;
					sendToOfflineCounter = 10;
				} else {
					offline = false;
				}
				Server.asyncPool().execute(new AsyncTask() {
					@Override
					public void run() {
						process(offline);
					}

					@Override
					public String name() {
						return "UnionUpdate";
					}
				});
			}
		});
	}

	public UnionDataProvider provider() {
		return provider;
	}

	public Predicate<UnionServer> statusSendFilter() {
		return statusSendFilter;
	}

	public void setStatusSendFilter(Predicate<UnionServer> statusSendFilter) {
		this.statusSendFilter = statusSendFilter;
	}

	public void setProvider(UnionDataProvider provider) {
		this.provider = provider;
	}

	public IntSupplier onlineCalculator() {
		return onlineCalculator;
	}

	public void setOnlineCalculator(IntSupplier onlineCalculator) {
		this.onlineCalculator = onlineCalculator;
	}

	public UnionServer thisServer() {
		return thisServer;
	}

	public void shutdown () {
		updateTask.cancel();

		servers.values().forEach(server -> {
			UpdateStatus packet = new UpdateStatus();
			packet.isOnline = false;
			packet.onlineCount = 0;
			server.sendPacket(packet);
		});
	}

	private void process (boolean sendToOffline) {
		long now = System.currentTimeMillis();
		int online = onlineCalculator().getAsInt();
		servers.values().forEach(server -> {
			if (statusSendFilter.test(server)) {
				server.update(now, online, sendToOffline);
			}
		});
	}

	public Collection<UnionServer> servers () {
		return servers.values();
	}

	public void registerPacket (UnionPacket packet) {
		packets[packet.id()] = packet.clone();
	}

	public void addServer (UnionServer server) {
		var tmp = new HashMap<>(servers);
		if (tmp.put(server.address, server) != null) {
			throw new IllegalArgumentException();
		}
		var tmp2 = new HashMap<>(serversById);
		if (tmp2.put(server.id, server) != null) {
			throw new IllegalArgumentException();
		}
		servers = tmp;
		serversById = tmp2;
	}

	public void removeServer (UnionServer server) {
		var tmp = new HashMap<>(servers);
		tmp.remove(server.address);
		var tmp2 = new HashMap<>(serversById);
		tmp2.remove(server.id);
		servers = tmp;
		serversById = tmp2;
	}

	public UnionServer getServer (InetSocketAddress address) {
		return servers.get(address);
	}

	public UnionServer getServer (String id) {
		return serversById.get(id);
	}

	@Override
	public boolean isValidPacket(NioBuffer buf) {
		if (!buf.isReadable(2)) {
			return false;
		}
		int id = buf.getUByte(buf.readerIdx());
		return id == PacketIds.RAKNET_ID;
	}

	@Override
	public boolean handle(Consumer<NioBuffer> reply, InetSocketAddress address, NioBuffer buf) {
		UnionServer server = getServer(address);
		if (server == null) {
			return false;
		}

		buf.skipReading(1);
		int id = buf.readUByte();
		UnionPacket packet = packets[id];
		if (packet == null) {
			return false;
		}

		packet = packet.clone();
		packet.decode(buf);
		return server.handle(packet);
	}
}
