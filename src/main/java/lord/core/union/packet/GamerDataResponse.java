package lord.core.union.packet;

import beengine.nbt.NbtMap;
import beengine.util.binary.NioBuffer;
import lombok.ToString;
import lord.core.union.UnionServer;

@ToString
public class GamerDataResponse extends UnionPacket {

	public enum Status {
		ALLOW,
		DUPLICATE,
		BANNED
	}

	public long requestId;
	public Status status;
	public long bannedUntil;
	public NbtMap gamerData;

	@Override
	public int id() {
		return PacketIds.GAMER_AUTH_STATUS_RESPONSE;
	}

	@Override
	public void encode(NioBuffer out) {
		out.writeLong(requestId);
		out.writeByte(status.ordinal());
		if (status == Status.ALLOW) {
			writeNbt(out, gamerData);
		}
		if (status == Status.BANNED) {
			out.writeLong(bannedUntil);
		}
	}

	@Override
	public void decode(NioBuffer in) {
		requestId = in.readLong();
		status = Status.values()[in.readUByte()];
		if (status == Status.ALLOW) {
			gamerData = readNbt(in);
		}
		if (status == Status.BANNED) {
			bannedUntil = in.readLong();
		}
	}

	@Override
	public boolean handle(UnionPacketHandler handler, UnionServer server) {
		return handler.handle(this, server);
	}
}
