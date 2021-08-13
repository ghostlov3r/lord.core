package lord.core.listener;

import cn.nukkit.event.player.PlayerQuitEvent;
import lord.core.LordCore;
import lord.core.api.CoreApi;
import lord.core.listener.service.EvH;

public class PlayerQuitListener implements EvH<PlayerQuitEvent> {
	
	private LordCore core = CoreApi.getCore();
	
	@Override
	public void handle (PlayerQuitEvent event) {
		event.setQuitMessage("");
		
		core.gamerMan().allGamers().remove(event.getPlayer().getName());
		core.gamerMan().gamers().remove(event.getPlayer().getName());
	}
	
}
