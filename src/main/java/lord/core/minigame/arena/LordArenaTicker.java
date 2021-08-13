package lord.core.minigame.arena;

import cn.nukkit.scheduler.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class LordArenaTicker extends Task {
	
	/** True == Начало Wait-отсчета, менять статус не нужно */
	private       boolean   isWaitStart = true;
	private       int       second;
	private final LordArena arena;
	
	/** Вернет True, если больше повторений таска не требуется */
	public boolean switchType () {
		if (arena.state == ArenaState.GAME_END) {
			arena.onGameEndStateEnded_internal();
			arena.cancelTask();
			return true;
		}
		if (isWaitStart) isWaitStart = false;
		else arena.state = arena.getState().getNext();
		
		second = arena.getActualDurations().getDuration(arena.state);
		
		switch (arena.state) {
			case WAIT:     arena.onWaitStart (second); break;
			case WAIT_END: arena.onWaitEnd   (second); break;
			case PRE_GAME: arena.onPreGame   (second); break;
			case GAME:     arena.onGameStart (second); break;
			case GAME_END: arena.onGameEnd   (second); break;
		}
		return false;
	}
	
	@Override
	public void onRun (int i) {
		if (second <= 0) {
			if (switchType()) return;
		}
		arena.onTick(second);
		
		switch (arena.state) {
			case WAIT:     arena.onWaitTick    (second); break;
			case WAIT_END: arena.onWaitEndTick (second); break;
			case PRE_GAME: arena.onPreGameTick (second); break;
			case GAME:     arena.onGameTick    (second); break;
			case GAME_END: arena.onGameEndTick (second); break;
		}
		second = second - 1;
	}
	
}
