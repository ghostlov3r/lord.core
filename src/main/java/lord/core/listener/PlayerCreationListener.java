package lord.core.listener;

import cn.nukkit.event.player.PlayerCreationEvent;
import lord.core.LordCore;
import lord.core.api.CoreApi;
import lord.core.listener.service.EvH;

/**
 * Заменяет класс игрока на Gamer
 */
public class PlayerCreationListener implements EvH<PlayerCreationEvent> {
	
	private LordCore core = CoreApi.getCore();
	
	@Override
	public void handle (PlayerCreationEvent playerCreationEvent) {
		if (core.gamerMan() == null) {
			core.getLogger().critical("GamerDataManager is Null !");
			core.getServer().shutdown();
			return;
		}
		if (core.gamerMan().getPlayerClass() == null) {
			core.getLogger().critical("Class for player creation is Null !");
			core.getServer().shutdown();
			return;
		}
		playerCreationEvent.setPlayerClass(core.gamerMan().getPlayerClass());
	}
	
}
