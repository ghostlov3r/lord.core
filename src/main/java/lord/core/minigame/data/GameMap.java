package lord.core.minigame.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.ghostlov3r.beengine.utils.config.Config;
import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.beengine.world.WorldManager;
import dev.ghostlov3r.common.DiskEntry;
import dev.ghostlov3r.common.DiskMap;
import dev.ghostlov3r.common.concurrent.Promise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMap extends DiskEntry<String> {

	public String worldName;

	public Map<String, MapTeam> teams = new HashMap<>();

	@JsonIgnore
	private WorldFactory factory;

	public GameMap(DiskMap<String, ?> map, String key) {
		super(map, key);
	}

	public void init () {
		factory = new WorldFactory(WorldManager.get().getWorldPath(worldName));
	}

	public WorldFactory worldFactory () {
		return factory;
	}
}
