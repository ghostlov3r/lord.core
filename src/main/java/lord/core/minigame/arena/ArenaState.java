package lord.core.minigame.arena;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lord.core.minigame.LordArenaMan;
import lord.core.minigame.MiniGameConfig;
import lord.core.util.Util;

/**
 * Варианты состояния Lord-Арены.
 * Каждое состояние имеет ссылку на следующее.
 * Последнее состояние имеет ссылку на первое.
 *
 * @author ghostlov3r
 */
@Getter
public enum ArenaState {
	
	WAIT,  WAIT_END,  PRE_GAME,  GAME,  GAME_END,  RELOAD;
	
	@Setter private String     text      = "";
	@Setter private String     extraText = "";
			private ArenaState next      = null;
	
	private ArenaState finup (MiniGameConfig config, ArenaState next) {
		this.next = next;
		this.text = (String) Util.invoke(config, "getState" + Util.upperNameToCamel(this.name()));
		return this;
	}
	
	public static void init (LordArenaMan<?, ?, ?> manager) {
		val cfg = manager.getConfig();
		
		WAIT.finup(cfg,
			WAIT_END.finup(cfg,
				PRE_GAME.finup(cfg,
					GAME.finup(cfg,
						GAME_END.finup(cfg,
							RELOAD.finup(cfg, WAIT))))));
	}
	
}
