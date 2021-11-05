package lord.core.minigame;

import dev.ghostlov3r.beengine.utils.config.Config;
import dev.ghostlov3r.beengine.utils.config.Name;
import lombok.Getter;

/**
 * Базовая конфигурация миниигры
 */
@Name("mg_config")
public abstract class MiniGameConfig extends Config {

	public String stateStandBy = "Ожидание";
	public String stateStandByExtra = "";

	public String stateWait = "Ожидание";
	public String stateWaitExtra = "";
	
	public String stateWaitEnd = "Игра начинается...";
	public String stateWaitEndExtra = "";
	
	public String statePreGame = "Игра начинается...";
	public String statePreGameExtra = "";
	
	public String stateGame = "Идёт игра";
	public String stateGameExtra = "";
	
	public String stateGameEnd = "Конец игры";
	public String stateGameEndExtra = "";
	
	/* ======================================================================================= */

	public String waitLobbyName;

	public String menuItemDecorSymbol = "◆";
}
