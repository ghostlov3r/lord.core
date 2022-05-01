package lord.core.util;

import beengine.command.Command;
import beengine.command.CommandSender;
import beengine.util.TextFormat;
import lord.core.gamer.Gamer;

public abstract class LordCommand extends Command {

	public LordCommand(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] strings) {
		if (commandSender instanceof Gamer gamer) {
			execute(gamer, strings);
		}
		else {
			commandSender.sendMessage(TextFormat.RED+"Use command in game");
		}
		return true;
	}

	public abstract void execute (Gamer gamer, String[] args);
}
