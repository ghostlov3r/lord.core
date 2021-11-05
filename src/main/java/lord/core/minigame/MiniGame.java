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
import lord.core.LordCore;
import lord.core.minigame.arena.Arena;
import lord.core.minigame.arena.ArenaState;
import lord.core.minigame.data.*;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})

public abstract class MiniGame<
		TMap extends GameMap,
		TArena extends Arena,
		TGamer extends MGGamer>
{
	private DiskMap<String, TMap> maps;
	private Int2ReferenceMap<TArena> arenas;
	private DiskMap<Integer, ArenaInstance> instances;
	private DiskMap<String, ArenaType> arenaTypes;
	private World waitLobby;
	private MiniGameConfig config;
	private Path dataPath;

	public MiniGame () {
		dataPath = LordCore.instance().dataPath();
		config = MiniGameConfig.loadFromDir(dataPath, MiniGameConfig.class);

		Promise<World> waitLobbyPromise = WorldManager.get().loadWorld(config.waitLobbyName);
		waitLobbyPromise.onResolve(promise -> {
			waitLobby = promise.result();
		});

		arenaTypes = new DiskMap<>(dataPath.resolve("arena_types"), ArenaType.class);
		instances = new DiskMap<>(dataPath.resolve("arenas"), ArenaInstance.class);
		maps = new DiskMap<>(dataPath.resolve("maps"), (Class<TMap>) Utils.superGenericClass(this, 1));

		arenaTypes.loadAll();
		arenaTypes.values().forEach(type -> {
			type.matchMaps((Collection<GameMap>) maps.values());
		});

		ArenaState.init(config);

		Class<TArena> arenaClass = (Class<TArena>) Utils.superGenericClass(this, 2);

		arenas = new Int2ReferenceOpenHashMap<>();

		instances.loadAll();
		instances.values().forEach(instance -> {
			ArenaType type = arenaTypes.get(instance.type());
			TArena arena = Utils.newInstance(arenaClass, this, type, instance.key());
			instance.statePos().forEach(pos -> {
				if (WorldManager.get().defaultWorld().getBlock(pos) instanceof BlockSign sign) {
					arena.stateSigns().add(sign);
				}
			});
			arenas.put(arena.id(), arena);
			arena.updateSignState();
		});

		var stateSigns = new HashMap<Vector3, Integer>();
		instances.values().forEach(instance -> {
			instance.statePos().forEach(pos -> {
				stateSigns.put(pos, instance.key());
			});
		});
		EventManager.get().register(LordCore.instance(), new MiniGameListener(this, stateSigns,
				(Class<? extends MGGamer>) Utils.superGenericClass(this, 3)));

		Utils.waitFor(() -> {
			Server.asyncPool().collectTasks();
			return waitLobbyPromise.isResolved();
		});
	}

	public World waitLobby() {
		return waitLobby;
	}

	public MiniGameConfig config() {
		return config;
	}

	public Int2ReferenceMap<TArena> arenas() {
		return arenas;
	}
}
