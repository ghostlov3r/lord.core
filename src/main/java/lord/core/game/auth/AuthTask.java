package lord.core.game.auth;

import cn.nukkit.scheduler.Task;
import lord.core.gamer.Gamer;
import lord.core.LordCore;

public class AuthTask extends Task {
	
	private final Gamer player;
	private int counter = 0;
	
	public AuthTask (Gamer player) {
		this.player = player;
	}
	
	@Override
	public void onRun(int i) {
		if (!this.player.isOnline() || this.player.isAuthorized()) {
			this.cancel();
			return;
		}
		if (this.counter > 40) {
			this.player.delayedKick(LordCore.NEKRAFT_LN + "§c§lВремя авторизации ограничено");
			this.cancel();
			return;
		}
		this.player.sendTitle("§l§bАктивируйте приседание", "Бутылка отсутствует", 20, 20, 10);
		this.counter++;
	}
}
