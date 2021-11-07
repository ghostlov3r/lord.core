package lord.core.minigame;

import dev.ghostlov3r.beengine.Beengine;
import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.beengine.block.blocks.BlockSign;
import dev.ghostlov3r.beengine.event.EventManager;
import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.beengine.world.WorldManager;
import dev.ghostlov3r.common.DiskMap;
import dev.ghostlov3r.common.Utils;
import dev.ghostlov3r.common.concurrent.Promise;
import dev.ghostlov3r.math.Vector3;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lord.core.LordCore;
import lord.core.minigame.arena.Arena;
import lord.core.minigame.arena.ArenaState;
import lord.core.minigame.arena.Team;
import lord.core.minigame.data.*;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked"})

/*
 TODO
 - Улучшить scoreboard
 - Спектаторство
 */
public class MiniGame
{
	private DiskMap<String, GameMap> maps;
	private Int2ReferenceMap<Arena> arenas;
	private DiskMap<Integer, ArenaData> instances;
	private DiskMap<String, ArenaType> arenaTypes;
	private Map<Vector3, Integer> stateSigns;
	private World waitLobby;
	private MiniGameConfig config;
	private Path dataPath;

	private Class<? extends Arena> arenaType;
	private Class<? extends ArenaType> arenaTypeType;
	private Class<? extends Team> teamType;
	private Class<? extends GameMap> mapType;
	private Class<? extends MapTeam> mapTeamType;
	private Class<? extends ArenaData> dataType;
	private Class<? extends MiniGameConfig> configType;
	private Class<? extends Wizard> wizardType;
	private Class<? extends MGGamer> gamerType;

	private MiniGame (
			Class<? extends Arena> arenaType,
			Class<? extends ArenaType> arenaTypeType,
			Class<? extends Team> teamType,
			Class<? extends GameMap> mapType,
			Class<? extends MapTeam> mapTeamType,
			Class<? extends ArenaData> dataType,
			Class<? extends MiniGameConfig> configType,
			Class<? extends Wizard> wizardType,
			Class<? extends MGGamer> gamerType,
			Path dataPath
	) {
		this.arenaType = arenaType;
		this.arenaTypeType = arenaTypeType;
		this.teamType = teamType;
		this.mapType = mapType;
		this.mapTeamType = mapTeamType;
		this.dataType = dataType;
		this.configType = configType;
		this.wizardType = wizardType;
		this.gamerType = gamerType;
		this.dataPath = dataPath;

		config = MiniGameConfig.loadFromDir(dataPath, configType);

		Promise<World> waitLobbyPromise = WorldManager.get().loadWorld(config.waitLobbyName);
		waitLobbyPromise.onResolve(promise -> {
			waitLobby = promise.result();
		});

		maps = new DiskMap<>(dataPath.resolve("maps"), (Class<GameMap>) mapType);
		maps.loadAll();
		maps.values().removeIf(map -> {
			boolean worldExists = Files.exists(Beengine.WORLDS_PATH.resolve(map.worldName));
			if (!worldExists) {
				LordCore.log.info("Карта '"+map.displayName+"' не будет активна, так как мир '"+map.worldName+"' не найден");
				return true;
			}
			return false;
		});
		maps.values().forEach(map -> map.init(mapTeamType));

		arenaTypes = new DiskMap<>(dataPath.resolve("arena_types"), (Class<ArenaType>) arenaTypeType);
		arenaTypes.loadAll();
		updateTypes();

		ArenaState.init(config);

		arenas = new Int2ReferenceOpenHashMap<>();

		instances = new DiskMap<>(dataPath.resolve("arenas"), (Class<ArenaData>) dataType);
		instances.loadAll();
		instances.values().forEach(this::enableArena);

		stateSigns = new HashMap<>();
		instances.values().forEach(instance -> {
			instance.statePos().forEach(pos -> {
				stateSigns.put(pos, instance.key());
			});
		});
		EventManager.get().register(LordCore.instance(), new MiniGameListener(this, stateSigns, gamerType));

		Utils.waitFor(() -> {
			Server.asyncPool().collectTasks();
			return waitLobbyPromise.isResolved();
		});

		LordCore.log.info("Активно "+arenaTypes.size()+ " типов арен");
		for (ArenaType type : arenaTypes.values()) {
			LordCore.log.info("Карты типа "+type.key()+"("+type.maps().size()+"шт.): "+
					String.join(", ", type.maps().stream().map(map -> map.displayName+" (в мире "+map.worldName).toList()));
		}
	}

	public void addAvailableArena (ArenaType type, int id) {
		if (arenas.containsKey(id)) {
			throw new IllegalArgumentException();
		}
		ArenaData instance = instances.newValueFor(id);
		instance.type = type.key();
		instances.add(instance);
		instance.save();

		enableArena(instance);
	}

	public void addStateSign (Arena arena, BlockSign sign) {
		if (!stateSigns.containsKey(sign)) {
			stateSigns.put(sign.toVector(), arena.id());
			instances.get(arena.id()).statePos().add(sign.toVector());
			instances.get(arena.id()).save();
			arena.stateSigns().add(sign);
			arena.updateSignState();
		}
	}

	@Nullable
	public Arena getArenaBySign (Vector3 vec) {
		Integer arenaIdx = stateSigns.get(vec);
		if (arenaIdx != null) {
			return arenas.get(arenaIdx.intValue());
		} else {
			return null;
		}
	}

	@SneakyThrows
	private void enableArena (ArenaData instance) {
		ArenaType type = arenaTypes.get(instance.type());
		Arena arena = arenaType.getConstructor(MiniGame.class, ArenaType.class, int.class)
				.newInstance(this, type, instance.key());
		instance.statePos().forEach(pos -> {
			if (WorldManager.get().defaultWorld().getBlock(pos) instanceof BlockSign sign) {
				arena.stateSigns().add(sign);
			}
		});
		arenas.put(arena.id(), arena);
		arena.updateSignState();
	}

	public World waitLobby() {
		return waitLobby;
	}

	public MiniGameConfig config() {
		return config;
	}

	public Map<Integer, Arena> arenas() {
		return arenas;
	}

	public Map<String, ArenaType> arenaTypes() {
		return arenaTypes;
	}

	public Map<String, GameMap> maps() {
		return maps;
	}

	public void updateTypes () {
		maps.values().forEach(map -> {
			String mapType = map.teams.get(0).locations().size() + "x" + map.teams.size();
			if (!arenaTypes.containsKey(mapType)) {
				ArenaType newType = arenaTypes.newValueFor(mapType);
				arenaTypes.add(newType);
			}
		});
		arenaTypes.values().forEach(type -> {
			type.matchMaps(maps.values());
		});
	}

	@SneakyThrows
	public void startWizard (MGGamer gamer) {
		wizardType.getConstructor(MGGamer.class).newInstance(gamer);
	}

	public GameMap instantiateMap (String name) {
		GameMap map = maps.newValueFor(name);
		map.worldName = name;
		map.displayName = name;
		map.init(mapTeamType);
		return map;
	}

	@Nullable
	public Arena matchArenaForJoin () {
		if (arenas.isEmpty()) {
			return null;
		}
		for (Arena arena : arenas.values()) {
			if (arena.isJoinable()) {
				if (!arena.isEmpty()) {
					return arena;
				}
			}
		}
		for (Arena arena : arenas.values()) {
			if (arena.isJoinable()) {
				return arena;
			}
		}
		return null;
	}

	public Class<? extends Team> teamType() {
		return teamType;
	}

	public static Builder builder () {
		return new Builder();
	}

	@Accessors(fluent = true, chain = true)
	@Setter
	public static class Builder {
		private Class<? extends Arena> arenaType = Arena.class;
		private Class<? extends ArenaType> arenaTypeType = ArenaType.class;
		private Class<? extends Team> teamType = Team.class;
		private Class<? extends GameMap> mapType = GameMap.class;
		private Class<? extends MapTeam> mapTeamType = MapTeam.class;
		private Class<? extends ArenaData> dataType = ArenaData.class;
		private Class<? extends MiniGameConfig> configType = MiniGameConfig.class;
		private Class<? extends Wizard> wizardType = Wizard.class;
		private Class<? extends MGGamer> gamerType = MGGamer.class;
		private Path dataPath = Beengine.PLUGINS_DATA_PATH.resolve("game_data");

		public MiniGame build () {
			return new MiniGame(
				arenaType, arenaTypeType, teamType,
					mapType, mapTeamType, dataType,
					configType, wizardType, gamerType,
					dataPath
			);
		}
	}
}
