package lord.core.minigame.arena;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lord.core.minigame.arena.Team;

@Getter @RequiredArgsConstructor
public abstract class TeamStats<TTeam extends Team> {
	
	private final TTeam team;

}
