package lord.core.command.service;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import lord.core.LordCore;
import lord.core.gamer.Gamer;

/**
 * Модифицированная версия Nukkit команды
 * @author ghostlov3r
 */
public abstract class LordCommand extends Command {
	
	private boolean allowConsole = false;
	public final Server server;
	
	/**
	 * Команда регистрируется автоматически
	 * @param name Имя команды
	 */
	public LordCommand (String name) {
		super(name);
		this.server = LordCore.server;
		this.server.getCommandMap().register(name, this);
	}
	
	/** Разрешает использование в консоли */
	public void allowConsole () {
		this.allowConsole(true);
	}
	
	public void allowConsole (boolean value) {
		this.allowConsole = value;
	}
	
	@Override
	public boolean execute (CommandSender commandSender, String s, String[] strings) {
		if (!commandSender.isPlayer()) {
			if (!this.allowConsole) {
				commandSender.sendMessage("This is in-game addCommand!");
				return true;
			}
		}
		
		CmdArgs cmdArgs = new CmdArgs(strings, commandSender);
		
		try {
			return this.handle((Gamer) commandSender, cmdArgs);
		} catch (LordCmdException e) {
			commandSender.sendMessage(e.getMessage());
		} catch (Exception e) {
			commandSender.sendMessage("Сервер не может обработать команду.");
			e.printStackTrace(); // debug
		}
		
		return true;
	}
	
	public abstract boolean handle (Gamer player, CmdArgs args) throws LordCmdException;
}
