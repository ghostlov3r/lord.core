package lord.core.minigame.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.ghostlov3r.beengine.world.WorldManager;
import dev.ghostlov3r.common.DiskEntry;
import dev.ghostlov3r.common.DiskMap;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GameMap extends DiskEntry<String> {

	public String worldName;

	public String displayName;

	@JsonIgnore
	public List<MapTeam> teams = new ArrayList<>();

	@JsonIgnore
	private WorldFactory factory;

	private Class<? extends MapTeam> teamClass;

	public GameMap(DiskMap<String, ?> map, String key) {
		super(map, key);
	}

	@SneakyThrows
	public void init (Class<? extends MapTeam> teamKlass) {
		factory = new WorldFactory(WorldManager.get().getWorldPath(worldName));
		teamClass = teamKlass;
		if (Files.exists(path())) {
			ObjectMapper mapper = map().format().mapper();
			ArrayNode teamsNode = (ArrayNode) mapper.readTree(path().toFile()).get("teams");
			for (JsonNode teamNode : teamsNode) {
				teams.add(mapper.readValue(mapper.writeValueAsBytes(teamNode), teamClass));
			}
		}
	}

	public WorldFactory worldFactory () {
		return factory;
	}

	@SneakyThrows
	public MapTeam instantiateTeam () {
		return teamClass.getConstructor().newInstance();
	}

	@SneakyThrows
	@Override
	public void save() {
		super.save();
		ObjectMapper mapper = map().format().mapper();
		ObjectNode node = (ObjectNode) mapper.readTree(path().toFile());
		node.set("teams", (ArrayNode) mapper.readTree(mapper.writeValueAsBytes(teams)));
		mapper.writeValue(map().path().toFile(), node);
	}
}
