package lord.core.minigame.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lord.core.minigame.arena.LordArenaTeam;

@Getter @RequiredArgsConstructor
public abstract class TeamTempStats<Team extends LordArenaTeam> {
	
	private final Team team;
	
	/** Принимает значение True, когда команда
	 * выбыла из игры */
	@Setter private boolean droppedOut = false;

}
