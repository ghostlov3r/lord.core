package lord.core.listener;

import cn.nukkit.event.player.PlayerJoinEvent;
import lord.core.LordCore;
import lord.core.api.CoreApi;
import lord.core.gamer.Gamer;
import lord.core.listener.service.EvH;

public class PlayerJoinListener implements EvH<PlayerJoinEvent> {
	
	private LordCore core = CoreApi.getCore();
	
	@Override
	@SuppressWarnings("unchecked")
	public void handle (PlayerJoinEvent event) {
		event.setJoinMessage("");
		Gamer gamer = (Gamer) event.getPlayer();
		
		if (gamer.isAuthorized()) {
			gamer.onSuccessAuth();
		} else {
			core.auth().requestAuth(gamer);
		}
		
		gamer.joinTime = System.currentTimeMillis();
		
		core.gamerMan().allGamers().put(gamer.getName(), gamer); // todo redo async
	}
	
}
