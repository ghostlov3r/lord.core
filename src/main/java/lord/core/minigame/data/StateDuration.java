package lord.core.minigame.data;

import lombok.AllArgsConstructor;
import lord.core.minigame.arena.ArenaState;

@AllArgsConstructor
public class StateDuration {

	ArenaState state;
	int duration;

	public ArenaState state() {
		return state;
	}

	public int duration() {
		return duration;
	}
}
