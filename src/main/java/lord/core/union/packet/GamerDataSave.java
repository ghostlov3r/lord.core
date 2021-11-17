package lord.core.union.packet;

import dev.ghostlov3r.nbt.NbtMap;
import dev.ghostlov3r.raknet.PacketUtils;
import io.netty.buffer.ByteBuf;
import lord.core.union.UnionServer;

public class GamerDataSave extends UnionPacket {

	public String name;
	public NbtMap data;

	@Override
	public int id() {
		return PacketIds.GAMER_DATA_SAVE;
	}

	@Override
	public void encode(ByteBuf out) {
		PacketUtils.writeString(out, name);
		writeNbt(out, data);
	}

	@Override
	public void decode(ByteBuf in) {
		name = PacketUtils.readString(in);
		data = readNbt(in);
	}

	@Override
	public boolean handle(UnionPacketHandler handler, UnionServer server) {
		return handler.handle(this, server);
	}
}
