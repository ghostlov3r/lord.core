package lord.core.union.packet;

import beengine.util.binary.NioBuffer;
import lord.core.union.UnionServer;

public class GamerDataSaved extends UnionPacket{

	public String name;

	@Override
	public int id() {
		return PacketIds.GAMER_DATA_SAVED;
	}

	@Override
	public void encode(NioBuffer out) {
		out.writeString(name);
	}

	@Override
	public void decode(NioBuffer in) {
		name = in.readString();
	}

	@Override
	public boolean handle(UnionPacketHandler handler, UnionServer server) {
		return handler.handle(this, server);
	}
}
