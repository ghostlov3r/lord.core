package lord.core.rework;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.block.BlockBreakEvent;
import lord.core.gamer.Gamer;
import lord.core.game.region.Region;
import lord.core.listener.manager.LordListener;

public class BlockBreakListener extends LordListener<BlockBreakEvent> {
	
	@Override
	public void handle (BlockBreakEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
		if (!Region.playerCanBuild(name, event.getBlock())) {
			event.setCancelled(true);
			player.sendTip("РЕГИОН ПОД ЗАЩИТОЙ");
			return;
		}
		int reward = 0;
		int id = event.getBlock().getId();
		if (Block.COAL_ORE == id)    reward = 1;
		if (Block.IRON_ORE == id)    reward = 2;
		if (Block.GOLD_ORE == id)    reward = 3;
		if (Block.DIAMOND_ORE == id) reward = 4;
		if (Block.EMERALD_ORE == id) reward = 5;
		
		if (reward != 0) {
			Gamer.get(name).addMoney(reward);
			player.sendTip("+ " + reward + " Koins");
		}
	}
	
}
