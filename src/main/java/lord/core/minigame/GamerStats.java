package lord.core.minigame;

import lombok.Getter;
import lombok.Setter;
import lord.core.minigame.arena.Team;

/**
 * Временные данные игрока, актуальные только во время ArenaState == GAME
 *
 * @param <TGamer> Тип игрока
 * @param <TTeam> Тип игровой команды игрока
 */
@Getter
public abstract class GamerStats<TGamer extends MGGamer, TTeam extends Team> {
	
	private final TGamer gamer;
	
	private final TTeam team;
	
	/** Принимает значение True, когда игрок проиграл
	 * и перешел в режим спектатора */
	@Setter private boolean droppedOut;
	
	@SuppressWarnings("unchecked")
	public GamerStats(TGamer gamer) {
		this.gamer = gamer;
		this.team = (TTeam) gamer.team();
		this.droppedOut = false;
	}
	
}
