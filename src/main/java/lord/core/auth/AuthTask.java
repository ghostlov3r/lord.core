package lord.core.auth;

import dev.ghostlov3r.beengine.scheduler.Task;
import lord.core.Lord;
import lord.core.gamer.Gamer;

public class AuthTask extends Task {
	
	private final Gamer player;
	private int counter = 0;
	boolean form;
	
	public AuthTask (Gamer player) {
		this.player = player;
		form = player.session().protocol().hasFormSupport();
	}
	
	@Override
	public void run() {
		++counter;
		if (!this.player.isOnline() || this.player.authChecked) {
			this.cancel();
			return;
		}
		if (player.handlingPassword) {
			return;
		}
		if (this.counter > 45) {
			this.player.disconnect(Lord.instance.config().getNameLN() + "§cВремя авторизации ограничено");
			this.cancel();
			return;
		}
		if ((counter % 3) == 0) {
			if (form) {
				this.player.sendMessage(">> §bАктивируйте приседание");
			}
			else {
				if (player.isRegistered()) {
					this.player.sendMessage(">> §bОтправьте в чат ваш пароль");
				}
				else {
					if (player.tempRegPassNoForm == null) {
						this.player.sendMessage(">> §bПридумайте пароль и напишите в чат");
					}
				}
			}
		}
	}
}
