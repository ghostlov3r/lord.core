package lord.core.union;

import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.beengine.network.RawPacketHandler;
import dev.ghostlov3r.beengine.scheduler.AsyncTask;
import dev.ghostlov3r.beengine.scheduler.Scheduler;
import dev.ghostlov3r.beengine.scheduler.TaskControl;
import io.netty.buffer.ByteBuf;
import lord.core.Lord;
import lord.core.union.packet.*;

import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class UnionHandler implements RawPacketHandler {

	private Map<InetSocketAddress, UnionServer> servers = new HashMap<>();
	private Map<Integer, UnionServer> serversById = new HashMap<>();
	private UnionConfig config;
	private UnionPacket[] packets = new UnionPacket[64];
	private TaskControl updateTask;
	private UnionDataProvider provider = new UnionDataProvider();
	private IntSupplier onlineCalculator = () -> Server.unsafe().playerList().size();

	public UnionHandler() {
		config = UnionConfig.loadFromDir(Lord.instance.dataPath(), UnionConfig.class);
		if (!Files.exists(config.file())) {
			config.save();
		}
		config.servers.forEach(entry -> {
			UnionServer server = new UnionServer(entry.id, entry.name, entry.ip, entry.port);
			addServer(server);
		});

		registerPacket(new UpdateStatus());
		registerPacket(new GamerDataRequest());
		registerPacket(new GamerDataResponse());
		registerPacket(new GamerDataSave());

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

	public void setProvider(UnionDataProvider provider) {
		this.provider = provider;
	}

	public IntSupplier onlineCalculator() {
		return onlineCalculator;
	}

	public void setOnlineCalculator(IntSupplier onlineCalculator) {
		this.onlineCalculator = onlineCalculator;
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
			server.update(now, online, sendToOffline);
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

	public UnionServer getServer (int id) {
		return serversById.get(id);
	}

	@Override
	public boolean isValidPacket(ByteBuf buf) {
		if (!buf.isReadable(2)) {
			return false;
		}
		int id = buf.getUnsignedByte(buf.readerIndex());
		return id == PacketIds.RAKNET_ID;
	}

	@Override
	public boolean handle(Consumer<ByteBuf> reply, InetSocketAddress address, ByteBuf buf) {
		UnionServer server = getServer(address);
		if (server == null) {
			return false;
		}

		buf.skipBytes(1);
		int id = buf.readUnsignedByte();
		UnionPacket packet = packets[id];
		if (packet == null) {
			return false;
		}

		packet = packet.clone();
		packet.decode(buf);
		return server.handle(packet);
	}
}
