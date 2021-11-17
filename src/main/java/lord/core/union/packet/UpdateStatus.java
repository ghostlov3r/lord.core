package lord.core.union.packet;

import io.netty.buffer.ByteBuf;
import lord.core.union.UnionServer;

public class UpdateStatus extends UnionPacket {

	public boolean isOnline;
	public int onlineCount;

	@Override
	public int id() {
		return PacketIds.UPDATE_STATUS;
	}

	@Override
	public void encode(ByteBuf out) {
		out.writeBoolean(isOnline);
		if (isOnline) {
			out.writeShort(onlineCount);
		}
	}

	@Override
	public void decode(ByteBuf in) {
		isOnline = in.readBoolean();
		if (isOnline) {
			onlineCount = in.readShort();
		}
	}

	@Override
	public boolean handle(UnionPacketHandler handler, UnionServer server) {
		return handler.handle(this, server);
	}
}
