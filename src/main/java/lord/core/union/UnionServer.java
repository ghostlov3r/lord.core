package lord.core.union;

import dev.ghostlov3r.beengine.Beengine;
import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.common.buf.Allocator;
import dev.ghostlov3r.log.Logger;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import lord.core.union.packet.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UnionServer {

	public int id;
	public String name;
	public InetSocketAddress address;
	public int onlineCount;
	public boolean isOnline;
	public long lastOnlineUpdate;
	public UnionPacketHandler handler;
	public Logger logger;

	@SneakyThrows
	public UnionServer (int id, String name, String ip, int port) {
		this.id = id;
		this.name = name;
		this.address = new InetSocketAddress(InetAddress.getByName(ip), port);
		this.logger = Server.logger().withPrefix(name);
	}

	public void sendPacket (UnionPacket packet) {
		ByteBuf buf = Allocator.ioBuffer();
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
