package lord.core.minigame.arena;

import dev.ghostlov3r.beengine.scheduler.Task;
import lombok.Getter;
import lombok.experimental.Accessors;

@SuppressWarnings("rawtypes")
@Accessors(fluent = true)
@Getter
public class Ticker extends Task {
	
	private int second;
	private Arena arena;

	Ticker (Arena arena) {
		this.arena = arena;
		refreshSecond();
	}

	public void refreshSecond () {
		this.second = arena.type().durationOfState(arena.state());
	}

	@Override
	public void run () {
		if (second == 0 && arena.state() == ArenaState.WAIT_END && arena.gameWorld() == null) {
			arena.logger.warning("World for arena #"+arena.id()+" is not ready yet");
			return;
		}

		if (--second == 0) {
			arena.setState(arena.state().next());
		}
		arena.onTick(second);
	}
	
}
