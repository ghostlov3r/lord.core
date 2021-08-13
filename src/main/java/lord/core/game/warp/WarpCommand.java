package lord.core.game.warp;

import lord.core.command.service.CmdArgs;
import lord.core.command.service.LordCmdException;
import lord.core.command.service.LordCommand;
import lord.core.gamer.Gamer;

public class WarpCommand extends LordCommand {
	
	/**
	 * Команда регистрируется автоматически
	 */
	public WarpCommand () {
		super("warp");
		setDescription("Точки телепортации");
	}
	
	@Override
	public boolean handle (Gamer player, CmdArgs args) throws LordCmdException {
		if (args.count() == 1) {
			// тп на варп
		}
		if (args.count() == 0) {
			// todo
		}
		return true;
	}
}
