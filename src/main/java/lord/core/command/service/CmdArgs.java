package lord.core.command.service;

import cn.nukkit.command.CommandSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lord.core.gamer.Gamer;

/**
 * Оболочка аргументов команды для LordCommand
 * @author ghostlov3r
 */
@RequiredArgsConstructor
public class CmdArgs {
	
	private final String[] args;
	
	@Getter
	private final CommandSender sender;
	
	/**
	 * @return Число введенных аргументов
	 */
	public int count () {
		return this.args.length;
	}
	
	/**
	 * @param n Номер аргумента
	 * @return Аргумент под указанным номером
	 */
	public String get (int n) {
		return this.args[n - 1];
	}
	
	/**
	 * @param n Номер аргумента
	 * @return Аргумент как целое
	 * @throws LordCmdException Если аргумент ввел число
	 */
	public int getInt (int n) throws LordCmdException {
		try {
			return Integer.parseInt(this.args[n - 1]);
		} catch (NumberFormatException e) {
			// this.sender.sendMessage("Параметр команды номер " + n + " должен быть числом.");
			throw new LordCmdException("Ожидалось целое число, но вы ввели " + this.args[n - 1]);
		}
	}
	
	/**
	 * @param n Номер аргумента
	 * @return Игрок с ником аргумента
	 * @throws LordCmdException Если игрок с таким ником не в сети
	 */
	public Gamer getGamer (int n) throws LordCmdException {
		Gamer gamer = (Gamer) sender.getServer().getPlayer(this.args[n - 1]);
		if (gamer == null) {
			throw new LordCmdException("Игрок с именем " + this.args[n - 1] + " сейчас не играет");
		}
		return gamer;
	}
	
}
