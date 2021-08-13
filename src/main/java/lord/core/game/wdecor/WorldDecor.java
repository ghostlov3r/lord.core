package lord.core.game.wdecor;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.NukkitRandom;
import lord.core.api.TaskApi;

import java.util.Random;

// todo двинуть сюда методы посадки дерева
public class WorldDecor {
	
	protected static final int MIN_Y = 0;
	protected static final int MAX_Y = 256;
	
	protected static Random random = new Random();
	protected static NukkitRandom nukkitRandom = new NukkitRandom();
	
	public void delayedDecorate (FullChunk chunk) {
		TaskApi.delay(1, new WorldDecorTask(chunk, this));
	}
	
}
