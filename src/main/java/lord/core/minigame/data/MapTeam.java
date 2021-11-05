package lord.core.minigame.data;

import java.util.ArrayList;
import java.util.List;

public class MapTeam {

	List<WeakLocation> spawnLocations = new ArrayList<>();

	public List<WeakLocation> locations () {
		return spawnLocations;
	}

}
