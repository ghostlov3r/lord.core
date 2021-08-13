package lord.core.minigame.mggamer;

import lombok.Getter;
import lombok.Setter;
import lord.core.minigame.arena.LordArenaTeam;
import org.jetbrains.annotations.NotNull;

/**
 * Временные данные игрока, актуальные только во время ArenaState == GAME
 *
 * @param <G> Тип игрока
 * @param <Team> Тип игровой команды игрока
 */
@Getter
public abstract class GamerTempStats<G extends MGGamer, Team extends LordArenaTeam> {
	
	private final G gamer;
	
	private final Team team;
	
	/** Принимает значение True, когда игрок проиграл
	 * и перешел в режим спектатора */
	@Setter private boolean droppedOut;
	
	@SuppressWarnings("unchecked")
	public GamerTempStats (@NotNull G gamer) {
		this.gamer = gamer;
		this.team = (Team) gamer.getTeam();
		this.droppedOut = false;
	}
	
}
