package lord.core.rework;

import cn.nukkit.event.block.BlockPlaceEvent;
import lord.core.game.region.Region;
import lord.core.listener.manager.LordListener;

public class BlockPlaceListener extends LordListener<BlockPlaceEvent> {
	
	@Override
	public void handle (BlockPlaceEvent event) {
		if (!Region.playerCanBuild(event.getPlayer().getName(), event.getBlock())) {
			event.setCancelled(true);
			event.getPlayer().sendTip("Регион под защитой");
		}
	}
	
}
