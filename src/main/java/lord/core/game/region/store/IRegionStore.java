package lord.core.game.region.store;

import cn.nukkit.block.Block;
import lord.core.game.region.Region;

import java.util.function.Consumer;

public interface IRegionStore {
	
	Region getRegion (Block block);
	
	Region getRegion (int x, int z);
	
	boolean containsRegion (int x, int z);
	
	void putRegionToMap (Region region);
	
	void removeRegionFromMap (Region region);
	
	void removeRegionFromMap (int x, int z);
	
	int getCount ();
	
	void forEach (Consumer<Region> action);
	
	void saveToFiles ();
	
}
