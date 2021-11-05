package lord.core.game.kit;

import lord.core.gamer.Gamer;
import lord.core.util.LordCommand;

public class KitCommand extends LordCommand {
	
	private KitMan manager;
	
	public KitCommand (KitMan manager) {
		super("kit");
		this.manager = manager;
	}

	@Override
	public void execute(Gamer gamer, String[] args) {

	}
}
