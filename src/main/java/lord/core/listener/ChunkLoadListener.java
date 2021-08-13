package lord.core.listener;

import cn.nukkit.event.level.ChunkLoadEvent;
import lord.core.LordCore;
import lord.core.listener.service.EvH;

public class ChunkLoadListener implements EvH<ChunkLoadEvent> {
	
	@Override
	public void handle (ChunkLoadEvent event) {
		if (event.isNewChunk()) {
			if (event.getChunk().getProvider().getLevel().getName().equals("world")) { // rework later
				LordCore.log.info("Загружен новый чанк в world");
				// WorldDecorTask.delayedDecorate(event.getChunk());
			}
		}
	}
	
}
