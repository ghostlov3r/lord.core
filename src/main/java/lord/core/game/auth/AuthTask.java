package lord.core.game.auth;

import dev.ghostlov3r.beengine.scheduler.Task;
import lord.core.gamer.Gamer;
import lord.core.LordCore;

public class AuthTask extends Task {
	
	private final Gamer player;
	private int counter = 0;
	
	public AuthTask (Gamer player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		if (!this.player.isOnline() || this.player.authorized()) {
			this.cancel();
			return;
		}
		if (this.counter > 40) {
			this.player.delayedKick(LordCore.instance().config().getNameLN() + "§c§lВремя авторизации ограничено");
			this.cancel();
			return;
		}
		this.player.sendTitle("§l§bАктивируйте приседание", "Бутылка отсутствует", 20, 20, 10);
		this.counter++;
	}
}
