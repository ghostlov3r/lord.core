package lord.core.rework;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import lord.core.api.CoreApi;
import lord.core.gamer.Gamer;
import lord.core.listener.manager.LordListener;

public class EntityDamageListener extends LordListener<EntityDamageEvent> {
	
	@Override
	public void handle (EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		Gamer gamer = Gamer.get(entity.getName());
		if (!gamer.authorized) {
			event.setCancelled();
			return;
		}
		if (event.getDamage() >= entity.getHealth()) {
			event.setCancelled(true);
			CoreApi.simulateDeath(gamer);
		}
	}
	
}
