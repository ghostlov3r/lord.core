package lord.core.game.warp;

import lord.core.gamer.Gamer;
import lord.core.util.LordCommand;

public class WarpCommand extends LordCommand {
	
	/**
	 * Команда регистрируется автоматически
	 */
	public WarpCommand () {
		super("warp");
		setDescription("Точки телепортации");
	}
	
	@Override
	public void execute (Gamer player, String[] args) {
		if (args.length == 1) {
			// тп на варп
		}
		if (args.length == 0) {
			// todo
		}
	}
}
