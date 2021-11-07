package lord.core.minigame;

import lord.core.minigame.arena.Team;

/**
 * Временные данные игрока, актуальные только во время ArenaState == GAME
 */
public class GamerGameContext {
	
	private final MGGamer gamer;
	
	private final Team team;

	DeadGamer dead;
	
	/** Принимает значение True, когда игрок проиграл
	 * и перешел в режим спектатора */
	private boolean droppedOut;
	
	public GamerGameContext(MGGamer gamer) {
		this.gamer = gamer;
		this.team = gamer.team();
		this.droppedOut = false;
	}

	public Team team() {
		return team;
	}

	public MGGamer gamer() {
		return gamer;
	}

	public void setDroppedOut(boolean droppedOut) {
		this.droppedOut = droppedOut;
	}

	public boolean isDroppedOut() {
		return droppedOut;
	}
}
