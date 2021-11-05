package lord.core.minigame.arena;

import dev.ghostlov3r.common.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lord.core.minigame.MiniGameConfig;

/**
 * Варианты состояния Lord-Арены.
 * Каждое состояние имеет ссылку на следующее.
 * Последнее состояние имеет ссылку на первое.
 *
 * @author ghostlov3r
 */
@Accessors(fluent = true)
@Getter
public enum ArenaState {
	
	STAND_BY, WAIT,  WAIT_END,  PRE_GAME,  GAME,  GAME_END;
	
	@Setter private String     text      = "";
	@Setter private String     extraText = "";
			private ArenaState next      = null;

	private void initData (MiniGameConfig config) {
		text = (String) Utils.invoke(config, "getState" + Utils.camelName(this));
		next = values()[(ordinal() + 1) % values().length];
	}
	
	public static void init (MiniGameConfig config) {
		for (ArenaState state : values()) {
			state.initData(config);
		}
	}
	
}
