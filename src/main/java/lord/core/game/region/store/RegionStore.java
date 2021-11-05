package lord.core.game.region.store;

import dev.ghostlov3r.math.Vector3;
import lord.core.game.region.Region;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** Используется для хранения загруженных регионов */
public class RegionStore implements IRegionStore {
	
	/** Загруженные регионы. Ключ: x_z */
	private final Map<String, Region> regions;
	
	public RegionStore () {
		this.regions = new HashMap<>();
	}
	
	/** Генерирует ключ для Мапы */
	public String toKey (int x, int z) {
		return x + "_" + z;
	}
	
	/** Возвращает регион по блоку */
	@Override
	public Region getRegion (Vector3 block) {
		return this.getRegion(block.chunkX(), block.chunkZ());
	}
	
	/** Возвращает регион по координатам чанка */
	@Override
	public Region getRegion (int x, int z) {
		return this.regions.get(this.toKey(x, z));
	}
	
	@Override
	public boolean containsRegion (int x, int z) {
		return this.regions.containsKey(this.toKey(x, z));
	}
	
	/** Добавляет регион в Мапу */
	@Override
	public void putRegionToMap (Region region) {
		this.regions.put(this.toKey(region.x, region.z), region);
	}
	
	/** Убирает регион из Мапы */
	@Override
	public void removeRegionFromMap (Region region) {
		this.removeRegionFromMap(region.x, region.z);
	}
	
	/** Убирает регион из Мапы */
	@Override
	public void removeRegionFromMap (int x, int z) {
		this.regions.remove(this.toKey(x, z));
	}
	
	/** Количество регионов */
	@Override
	public int getCount () {
		return this.regions.size();
	}
	
	@Override
	public void forEach (Consumer<Region> action) {
		this.regions.forEach((key, region) -> action.accept(region));
	}
	
	@Override
	public void saveToFiles () {
		this.forEach(Region::save);
	}
	
}
