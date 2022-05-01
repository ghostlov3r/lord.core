package lord.core.game.region.store;

import beengine.util.math.Vector3;
import lord.core.game.region.Region;

import java.util.function.Consumer;

public interface IRegionStore {
	
	Region getRegion (Vector3 pos);
	
	Region getRegion (int x, int z);
	
	boolean containsRegion (int x, int z);
	
	void putRegionToMap (Region region);
	
	void removeRegionFromMap (Region region);
	
	void removeRegionFromMap (int x, int z);
	
	int getCount ();
	
	void forEach (Consumer<Region> action);
	
	void saveToFiles ();
	
}
