package lord.core.union;

import beengine.Beengine;
import beengine.Server;
import beengine.util.binary.NioBuffer;
import beengine.util.log.Logger;
import lombok.SneakyThrows;
import lord.core.union.packet.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UnionServer {

	public String id;
	public String name;
	public InetSocketAddress address;
	public int onlineCount;
	public boolean isOnline;
	public long lastOnlineUpdate;
	public UnionPacketHandler handler;
	public Logger logger;

	@SneakyThrows
	public UnionServer (String id, String name, String ip, int port) {
		this.id = id;
		this.name = name;
		this.address = new InetSocketAddress(InetAddress.getByName(ip), port);
		this.logger = Server.logger().withPrefix(name);
	}

	public void sendPacket (UnionPacket packet) {
		NioBuffer buf = NioBuffer.getPooled();
		buf.writeByte(PacketIds.RAKNET_ID);
		buf.writeByte(packet.id());
		packet.encode(buf);
		if (Beengine.DEBUG && !(packet instanceof UpdateStatus)) {
			logger.debug("OUT >>> "+packet);
		}
		Server.network().sendPacket(address, buf);
	}

	public boolean handle (UnionPacket packet) {
		if (Beengine.DEBUG && !(packet instanceof UpdateStatus)) {
			logger.debug("IN <<< "+packet);
		}
		UnionPacketHandler h = handler;
		if (h != null) {
			return packet.handle(h, this);
		}
		return false;
	}

	public void update (long now, int online, boolean sendToOffline) {
		if (isOnline) {
			if (lastOnlineUpdate + HARDCODED_SERVER_TIMEOUT < now) {
				isOnline = false;
				Server.logger().warning("Server #%s (%s) is offline!", id, name);
			}
		}
		if (isOnline || sendToOffline) {
			UpdateStatus packet = new UpdateStatus();
			packet.isOnline = true;
			packet.onlineCount = online;
			sendPacket(packet);
		}
	}

	private static final int HARDCODED_SERVER_TIMEOUT = 20 * 1000;
}
