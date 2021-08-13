package lord.core.minigame.arena;

import lombok.RequiredArgsConstructor;
import lord.core.minigame.mggamer.MGGamer;

@RequiredArgsConstructor
public abstract class ArenaWinData<Arena extends LordArena, Team extends LordArenaTeam, G extends MGGamer> {
	
	private final Arena arena;
	
	private final Team winnerTeam;
	
	private final G bestGamer;
	
}
