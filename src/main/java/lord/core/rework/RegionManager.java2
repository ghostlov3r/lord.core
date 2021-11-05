package lord.core.rework;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import lord.core.LordCore;
import lord.core.game.region.Region;
import lord.core.util.Util;
import lord.core.util.file.AdvFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegionManager {
	
	private static final String PREFIX = "[Region] ";
	
	// Карта существующих регионов. Формат X -> Z -> Region
	public static Map<Integer, HashMap<Integer, Region>> regions = new HashMap<>();
	
	// Карта проверенных регионов на наличие в папке. Формат X -> Z
	private static Map<Integer, ArrayList<Integer>> checkedMap = new HashMap<>();
	
	/** Заносит регион в хранилище */
	public static void addRegion (Region region) {
		HashMap<Integer, Region> mapZ = regions.computeIfAbsent(region.x, k -> new HashMap<>());
		mapZ.put(region.z, region);
	}
	
	/** Если регион есть в хранилище, возращает его, иначе NULL */
	public static Region getRegion (int chunkX, int chunkZ) {
		HashMap<Integer, Region> mapZ = regions.get(chunkX);
		if (mapZ == null) {
			return null;
		}
		return mapZ.get(chunkZ);
	}
	
	/** Помечает регион как отсутствующий в папке */
	public static void addChecked (int chunkX, int chunkZ) {
		ArrayList<Integer> listZ = checkedMap.computeIfAbsent(chunkX, k -> new ArrayList<>());
		listZ.add(chunkZ);
	}
	
	/** TRUE Если регион помечен как отсутствующий в папке */
	public static boolean isChecked (int chunkX, int chunkZ) {
		ArrayList<Integer> arrayZ = checkedMap.get(chunkX);
		if (arrayZ == null) {
			return false;
		}
		return arrayZ.contains(chunkZ);
	}
	
	public static Region newRegion (String playerName, String regionName, int chunkX, int chunkZ) {
		return newRegion(playerName, regionName, chunkX, chunkZ, false);
	}
	
	/** Создает новый регион */
	public static Region newRegion (String playerName, String regionName, int chunkX, int chunkZ, boolean unsaveable) {
		Region region = new Region();
		
		region.owner = playerName;
		region.name = regionName;
		region.x = chunkX;
		region.z = chunkZ;
		
		region.pvp = true;
		region.build = false;
		region.chest = false;
		region.door = true;
		
		region.memberList = new ArrayList<>(3);
		region.memberList.add(playerName);
		
		region.dirty = true;
		region.service = unsaveable;
		return region;
	}
	
	/** Загружает регион из файла */
	private static Region loadRegion (AdvFile file) {
		Region region = Util.GSON.fromJson(file.readFast(), Region.class);
		addRegion(region);
		return region;
	}
	
	public static boolean playerCanBuild (String playerName, Block block) {
		return playerCanBuild(playerName, block.getFloorX(), block.getFloorZ());
	}
	
	public static boolean playerCanBuild (String playerName, int blockX, int blockZ) {
		int x = Util.toChunk(blockX);
		int z = Util.toChunk(blockZ);
		Server s = LordCore.server;
		Region region = getRegion(x, z);
		
		if (region != null) {
			s.broadcastMessage("Регион есть в regions");
			return region.isMember(playerName);
		}
		if (isChecked(x, z)) {
			s.broadcastMessage("Регион есть в checkedMap");
			return true;
		}
		AdvFile file = getRegionFile(x, z);
		if (file.exists()) {
			s.broadcastMessage("Есть файл региона, читаем и пишем в regions");
			region = loadRegion(file);
			return region.isMember(playerName);
		}
		s.broadcastMessage("Файла нет. Региона вообще нет. Пишем в checkedMap");
		addChecked(x, z);
		return true;
	}
	
	public static AdvFile getRegionFile (int x, int z) {
		return Folder.REGIONS.getFile("" + x + "_" + z + ".json");
	}
	
	public static void playerWantCreate (Player player, String name) {
		int x = Util.toChunk((int)player.getX());
		int z = Util.toChunk((int)player.getZ());
		
		if (getRegion(x, z) != null) {
			player.sendMessage("Здесь есть регион");
			return;
		}
		
		Region region = newRegion(player.getName(), name, x, z);
		addRegion(region);
		
		// LordPlayer.addRegion(gamer, x, z);
		highlight(region, player);
		player.sendMessage("Регион создан на текущих координатах.");
	}
	
	// тест выделения региона
	public static void highlight (Region region, Player player) {
		
		Vector3[] points = new Vector3[4];
		
		int y = (int) player.getY();
		
		int x1 = Util.fromChunk(region.x);
		int x2 = x1 + 15;
		int z1 = Util.fromChunk(region.z);
		int z2 = z1 + 15;
		
		points[0] = new Vector3(x1, y, z1);
		points[1] = new Vector3(x1, y, z2);
		points[2] = new Vector3(x2, y, z1);
		points[3] = new Vector3(x2, y, z2);
		
		Level level = player.getLevel();
		
		for (Vector3 pos : points) {
			for (int i = 0; i < 4; i++) {
				pos.add(0, i, 0);
				level.setBlock(pos, Block.get(Block.DIRT)); // сделать выдимым только создателю и убирать позже
			}
		}
		
	}
	
	public static final int CHUNK_RADIUS = 15;
	
	public static void protectSpawn () {
		long startTime = System.currentTimeMillis();
		int count = 0;
		
		for (int x = -CHUNK_RADIUS; x <= CHUNK_RADIUS; x++) {
			for (int z = -CHUNK_RADIUS; z <= CHUNK_RADIUS; z++) {
				addRegion(newRegion("Server", "" + x + z, x, z, true));
				count++;
			}
		}
		
		LordCore.write("Spawn protect (" + count + " regions) took " + (System.currentTimeMillis() - startTime) + " ms");
	}
	
}
