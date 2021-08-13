package lord.core.minigame.arena;

import lombok.val;
import lord.core.minigame.LordArenaMan;
import lord.core.minigame.MiniGameConfig;
import lord.core.util.Util;

import java.util.EnumMap;

/**
 * Этот класс содержит в себе актуальные длительсти состояний арены,
 * рассчитанные с учетом конфигурации арены и обшей конфигураци игры.
 *
 * @author ghostlov3r
 */
public class ActualArenaDurations {
	
	/** */
	private EnumMap<ArenaState, Integer> actualDurations = new EnumMap<>(ArenaState.class);
	
	public ActualArenaDurations (LordArena arena) {
		
		val arenaDurations = arena.getDurations();
		val config = (MiniGameConfig) ((LordArenaMan) arena.getManager()).getConfig();
		val configDurations = config.getDurations();
		
		for (val state : ArenaState.values()) {
			if (state == ArenaState.RELOAD) continue;
			
			addDuration(state,
				config.isOverrideArenaDurations()
				? configDurations
				: (getDurationFrom(state, arenaDurations) > 0 ? arenaDurations : configDurations)
			);
		}
		
	}
	
	private int getDurationFrom (ArenaState state, ArenaDurations source) {
		return (int) Util.getFieldValue(source, Util.upperNameToCamel(state.name()));
	}
	
	private void addDuration (ArenaState state, ArenaDurations source) {
		actualDurations.put(state, getDurationFrom(state, source));
	}
	
	/** Возвращает длительность в секундах для выбранного состояния арены */
	public int getDuration (ArenaState state) {
		return actualDurations.get(state);
	}
	
}
