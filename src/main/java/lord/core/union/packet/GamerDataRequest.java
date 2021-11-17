package lord.core.union.packet;

import dev.ghostlov3r.raknet.PacketUtils;
import io.netty.buffer.ByteBuf;
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
	public void encode(ByteBuf out) {
		out.writeLong(requestId);
		PacketUtils.writeString(out, name);
		PacketUtils.writeAddress(out, address);
	}

	@Override
	public void decode(ByteBuf in) {
		requestId = in.readLong();
		name = PacketUtils.readString(in);
		address = PacketUtils.readAddress(in);
	}

	@Override
	public boolean handle(UnionPacketHandler handler, UnionServer server) {
		return handler.handle(this, server);
	}
}
