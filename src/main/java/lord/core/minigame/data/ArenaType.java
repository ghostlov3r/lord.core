package lord.core.minigame.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.ghostlov3r.beengine.block.utils.DyeColor;
import dev.ghostlov3r.common.DiskEntry;
import dev.ghostlov3r.common.DiskMap;
import lord.core.minigame.arena.ArenaState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Этот класс содержит в себе данные о длительности каждого из состояний арены.
 * Состояние Wait является таковым еще до начала отсчета на арене.
 * @author ghostlov3r
 */

public class ArenaType extends DiskEntry<String> {
	
	List<StateDuration> durations = new ArrayList<>();

	/** Мин. число чел. для отсчета
	 * Если арена соло, то общее число; если командная, то чел. в команде */
	int minPlayers;

	@JsonIgnore
	int teamCount;

	@JsonIgnore
	int teamSlots;

	List<DyeColor> colors = new ArrayList<>();

	@JsonIgnore
	List<GameMap> maps = new ArrayList<>();

	public ArenaType(DiskMap<String, ?> map, String key) {
		super(map, key);
		durations.add(new StateDuration(ArenaState.WAIT, 30));
		durations.add(new StateDuration(ArenaState.WAIT_END, 5));
		durations.add(new StateDuration(ArenaState.PRE_GAME, 5));
		durations.add(new StateDuration(ArenaState.GAME, 180));
		durations.add(new StateDuration(ArenaState.WAIT_END, 20));

		teamSlots = Integer.parseInt(key.split(Pattern.quote("x"))[0]);
		teamCount = Integer.parseInt(key.split(Pattern.quote("x"))[1]);
	}

	public int durationOfState (ArenaState state) {
		for (StateDuration duration : durations) {
			if (duration.state == state) {
				return duration.duration;
			}
		}
		throw new IllegalArgumentException(state.name());
	}

	public int minPlayers() {
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public int teamCount() {
		return teamCount;
	}

	public int teamSlots() {
		return teamSlots;
	}

	public int maxPlayers() {
		return teamCount * teamSlots;
	}

	public List<GameMap> maps() {
		return maps;
	}

	public List<DyeColor> colors() {
		return colors;
	}

	public void matchMaps (Collection<GameMap> maps) {
		this.maps.clear();
		maps.forEach(map -> {
			if (isMapMatches(map)) {
				this.maps.add(map);
			}
		});
	}

	protected boolean isMapMatches (GameMap map) {
		return map.teams.size() == teamCount && map.teams.get(0).spawnLocations.size() == teamCount;
	}
}
