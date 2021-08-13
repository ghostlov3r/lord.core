package lord.core.game.kit;

import lord.core.command.service.CmdArgs;
import lord.core.command.service.LordCommand;
import lord.core.command.service.NotIntException;
import lord.core.gamer.Gamer;

public class KitCommand extends LordCommand {
	
	private KitMan manager;
	
	public KitCommand (KitMan manager) {
		super("kit");
		this.manager = manager;
	}
	
	@Override
	public boolean handle (Gamer gamer, CmdArgs args) throws NotIntException {
		return false;
	}
}
