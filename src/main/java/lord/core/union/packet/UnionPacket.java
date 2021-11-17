package lord.core.union.packet;

import dev.ghostlov3r.binary.OutputBuffer;
import dev.ghostlov3r.nbt.*;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import lord.core.union.UnionServer;

public abstract class UnionPacket implements Cloneable {

	public abstract int id ();

	public abstract void encode (ByteBuf out);

	public abstract void decode (ByteBuf in);

	public abstract boolean handle (UnionPacketHandler handler, UnionServer server);

	@SneakyThrows
	@Override
	public UnionPacket clone() {
		return (UnionPacket) super.clone();
	}

	protected static void writeNbt (ByteBuf out, NbtMap nbt) {
		OutputBuffer buffer = OutputBuffer.local();
		NbtEncoder.encode(NbtWriter.NETWORK, nbt, buffer);
		out.writeBytes(buffer.buffer(), 0, buffer.offset());
	}

	protected static NbtMap readNbt (ByteBuf in) {
		byte[] nbt = new byte[in.readableBytes()];
		in.readBytes(nbt);
		return NbtDecoder.decode(NbtReader.NETWORK, NbtType.COMPOUND, nbt);
	}
}
