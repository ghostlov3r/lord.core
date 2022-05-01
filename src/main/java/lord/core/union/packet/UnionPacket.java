package lord.core.union.packet;

import beengine.nbt.*;
import beengine.util.binary.NioBuffer;
import beengine.util.binary.OutputBuffer;
import lombok.SneakyThrows;
import lord.core.union.UnionServer;

public abstract class UnionPacket implements Cloneable {

	public abstract int id ();

	public abstract void encode (NioBuffer out);

	public abstract void decode (NioBuffer in);

	public abstract boolean handle (UnionPacketHandler handler, UnionServer server);

	@SneakyThrows
	@Override
	public UnionPacket clone() {
		return (UnionPacket) super.clone();
	}

	protected static void writeNbt (NioBuffer out, NbtMap nbt) {
		OutputBuffer buffer = OutputBuffer.local();
		NbtEncoder.encode(NbtWriter.NETWORK, nbt, buffer);
		out.write(buffer.trimmedBuffer());
	}

	protected static NbtMap readNbt (NioBuffer in) {
		return NbtDecoder.decode(NbtReader.NETWORK, NbtType.COMPOUND, in.read(in.readableCount()));
	}
}
