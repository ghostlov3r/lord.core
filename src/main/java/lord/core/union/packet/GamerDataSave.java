package lord.core.union.packet;

import beengine.nbt.NbtMap;
import beengine.util.binary.NioBuffer;
import lord.core.union.UnionServer;

public class GamerDataSave extends UnionPacket {

	public String name;
	public NbtMap data;

	@Override
	public int id() {
		return PacketIds.GAMER_DATA_SAVE;
	}

	@Override
	public void encode(NioBuffer out) {
		out.writeString(name);
		writeNbt(out, data);
	}

	@Override
	public void decode(NioBuffer in) {
		name = in.readString();
		data = readNbt(in);
	}

	@Override
	public boolean handle(UnionPacketHandler handler, UnionServer server) {
		return handler.handle(this, server);
	}
}
