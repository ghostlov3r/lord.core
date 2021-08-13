package lord.core.scoreboard.packet;

import cn.nukkit.network.protocol.DataPacket;

import java.util.ArrayList;
import java.util.List;

public class SetScorePacket extends DataPacket {
	
	public static final byte TYPE_CHANGE = 0;
	public static final byte TYPE_REMOVE = 1;
	
	public byte type;
	public List<ScoreInfo> entries;
	
	@Override
	public void decode() {
		this.type = (byte) this.getByte();
		this.entries = this.getScorePacketInfos();
	}
	
	@Override
	public void encode() {
		this.reset();
		this.putByte(this.type);
		this.putScorePacketInfos(this.entries);
	}
	
	@Override
	public byte pid() {
		return 0x6c;
	}
	
	public void putScorePacketInfos(List<ScoreInfo> info) {
		this.putUnsignedVarInt(info.size());
		for(ScoreInfo entry : info) {
			this.putVarLong(entry.scoreboardId);
			this.putString(entry.objectiveName);
			this.putLInt(entry.score);
			this.putByte(entry.addType);
			switch(entry.addType) {
				case 1:
				case 2:
					this.putEntityUniqueId(entry.entityId);
					break;
				case 3:
					this.putString(entry.fakePlayer);
			}
		}
	}
	
	public List<ScoreInfo> getScorePacketInfos(){
		List<ScoreInfo> info = new ArrayList<>();
		long length = this.getUnsignedVarInt();
		for(int i = 0; i <= (int) length; i++) {
			ScoreInfo entry = new ScoreInfo();
			entry.scoreboardId = this.getVarLong();
			entry.objectiveName = this.getString();
			entry.score = this.getLInt();
			if(this.type == 0) {
				entry.addType = (byte) this.getByte();
				switch(entry.addType) {
					case 1:
					case 2:
						entry.entityId = this.getEntityUniqueId();
						break;
					case 3:
						entry.fakePlayer = this.getString();
				}
			}
			info.add(entry);
		}
		return info;
	}
	
}