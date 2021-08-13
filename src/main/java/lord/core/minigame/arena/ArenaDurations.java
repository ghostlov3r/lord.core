package lord.core.minigame.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Этот класс содержит в себе данные о длительности каждого из состояний арены.
 * Состояние Wait является таковым еще до начала отсчета на арене.
 * @author ghostlov3r
 */
@Getter @NoArgsConstructor @AllArgsConstructor @Accessors(fluent = true)
public class ArenaDurations {
	
	private int wait, waitEnd, preGame, game, gameEnd;
	
	public static ArenaDurations createForMGConfig () {
		return new ArenaDurations(30, 5, 5, 180, 20);
	}
	
}
