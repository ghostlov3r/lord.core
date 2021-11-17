package lord.core.union.packet;

import dev.ghostlov3r.beengine.Server;
import lord.core.union.UnionServer;

public class UnionPacketHandler {

	public boolean handle (UpdateStatus packet, UnionServer server) {
		if (packet.isOnline) {
			server.onlineCount = packet.onlineCount;
		} else {
			server.onlineCount = 0;
		}
		if (!server.isOnline && packet.isOnline) {
			Server.logger().info("Server #%s (%s) is Online!", server.id, server.name);
		}
		else if (server.isOnline && !packet.isOnline) {
			Server.logger().warning("Server #%s (%s) is offline!", server.id, server.name);
		}
		server.isOnline = packet.isOnline;
		server.lastOnlineUpdate = System.currentTimeMillis();
		return true;
	}

	public boolean handle (GamerDataRequest packet, UnionServer server) {return false;}

	public boolean handle (GamerDataResponse packet, UnionServer server) {return false;}

	public boolean handle (GamerDataSave packet, UnionServer server) {return false;}
}
