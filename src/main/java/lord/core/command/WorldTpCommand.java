package lord.core.command;

import lord.core.command.service.CmdArgs;
import lord.core.command.service.LordCommand;
import lord.core.gamer.Gamer;

public class WorldTpCommand extends LordCommand {
	
	public WorldTpCommand () {
		super("worldtp");
		this.setDescription("Телепортация в другой мир");
		
	}
	
	@Override
	public boolean handle (Gamer player, CmdArgs args) {
		if (args.count() != 1) {
			return false;
		}
		
		String levelName = args.get(1);
		this.server.loadLevel(levelName);
		
		if (this.server.isLevelLoaded(levelName)) {
			player.switchLevel(this.server.getLevelByName(levelName));
		} else {
			player.sendMessage("Не удалось загрузить мир");
		}
		
		return true;
	}
	
}
