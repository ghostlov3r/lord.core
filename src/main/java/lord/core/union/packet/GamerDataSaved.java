package lord.core.union.packet;

import dev.ghostlov3r.raknet.PacketUtils;
import io.netty.buffer.ByteBuf;
import lord.core.union.UnionServer;

public class GamerDataSaved extends UnionPacket{

	public String name;

	@Override
	public int id() {
		return PacketIds.GAMER_DATA_SAVED;
	}

	@Override
	public void encode(ByteBuf out) {
		PacketUtils.writeString(out, name);
	}

	@Override
	public void decode(ByteBuf in) {
		name = PacketUtils.readString(in);
	}

	@Override
	public boolean handle(UnionPacketHandler handler, UnionServer server) {
		return handler.handle(this, server);
	}
}
