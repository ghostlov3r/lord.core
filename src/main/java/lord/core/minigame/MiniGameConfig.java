package lord.core.minigame;

import lombok.Getter;
import lord.core.mgrbase.entry.LordConfig;
import lord.core.minigame.arena.ArenaDurations;

/**
 * Базовая конфигурация миниигры
 * @param <ArenaMan>
 */
@Getter
public abstract class MiniGameConfig<ArenaMan extends LordArenaMan>
	extends LordConfig<ArenaMan> {
	
	private String stateWait = "Ожидание";
	private String stateWaitExtra = "";
	
	private String stateWaitEnd = "Игра начинается...";
	private String stateWaitEndExtra = "";
	
	private String statePreGame = "Игра начинается...";
	private String statePreGameExtra = "";
	
	private String stateGame = "Идёт игра";
	private String stateGameExtra = "";
	
	private String stateGameEnd = "Конец игры";
	private String stateGameEndExtra = "";
	
	private String stateReload = "Перезагрузка...";
	private String stateReloadExtra = "";
	
	/* ======================================================================================= */
	
	private ArenaDurations durations = ArenaDurations.createForMGConfig();
	
	private boolean overrideArenaDurations = false;
	
	/* ======================================================================================= */
	
	private boolean backupWorlds = false;
	
}
