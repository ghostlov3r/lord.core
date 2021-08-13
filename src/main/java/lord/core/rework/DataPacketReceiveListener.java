package lord.core.rework;

import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.*;
import lord.core.LordCore;
import lord.core.listener.manager.LordListener;

public class DataPacketReceiveListener extends LordListener<DataPacketReceiveEvent> {
	
	@Override
	public void handle (DataPacketReceiveEvent event) {
		String message = event.getPacket().getClass().getSimpleName();
		int type = event.getPacket().pid();
		
		// DEBUG
		if (ProtocolInfo.PLAYER_ACTION_PACKET == type) {
			PlayerActionPacket packet = (PlayerActionPacket) event.getPacket();
			message += " | ACTION: " + packet.action;
			// return;
		}
		// TEST // Опасно
		if (ProtocolInfo.MOVE_PLAYER_PACKET == type) {
			MovePlayerPacket packet = (MovePlayerPacket) event.getPacket();
			message += " | MODE: " + packet.mode;
			if (packet.mode == MovePlayerPacket.MODE_NORMAL) {
				if (this.skippedMoves >= 3) {
					this.skippedMoves = 0;
				} else {
					this.skippedMoves++;
					event.setCancelled();
					message += " (SKIPPED)";
				}
			}
			// return;
		}
		
		LordCore.write("GOT PACKET: " + message);
	}
	
	// TEST
	private int skippedMoves = 0;
	
}
