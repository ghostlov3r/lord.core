package lord.core.union.packet;

import beengine.util.binary.NioBuffer;
import lombok.ToString;
import lord.core.union.UnionServer;

import java.net.InetSocketAddress;

@ToString
public class GamerDataRequest extends UnionPacket {

	public long requestId;
	public String name;
	public InetSocketAddress address;

	@Override
	public int id() {
		return PacketIds.GAMER_AUTH_STATUS_REQUEST;
	}

	@Override
	public void encode(NioBuffer out) {
		out.writeLong(requestId);
		out.writeString(name);
		out.writeAddress(address);
	}

	@Override
	public void decode(NioBuffer in) {
		requestId = in.readLong();
		name = in.readString();
		address = in.readAddress();
	}

	@Override
	public boolean handle(UnionPacketHandler handler, UnionServer server) {
		return handler.handle(this, server);
	}
}
