package lord.core.scoreboard.packet;

import cn.nukkit.network.protocol.DataPacket;

public class SetDisplayObjectivePacket extends DataPacket {
	
	public String displaySlot;
	public String objectiveName;
	public String displayName;
	public String criteriaName;
	public int sortOrder;
	
	@Override
	public void decode() {
		this.displaySlot = this.getString();
		this.objectiveName = this.getString();
		this.displayName = this.getString();
		this.criteriaName = this.getString();
		this.sortOrder = this.getVarInt();
		
	}
	
	@Override
	public void encode() {
		this.reset();
		this.putString(this.displaySlot);
		this.putString(this.objectiveName);
		this.putString(this.displayName);
		this.putString(this.criteriaName);
		this.putVarInt(this.sortOrder);
	}
	
	@Override
	public byte pid() {
		return 0x6b;
	}
	
}