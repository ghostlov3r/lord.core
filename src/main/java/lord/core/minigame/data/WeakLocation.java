package lord.core.minigame.data;

import dev.ghostlov3r.beengine.block.Position;
import dev.ghostlov3r.beengine.entity.util.Location;
import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.math.Vector3;

public class WeakLocation {

	public float x;
	public float y;
	public float z;
	public float yaw;
	public float pitch;

	public Vector3 asVector () {
		return new Vector3(x, y, z);
	}

	public Vector3 asPosition (World world) {
		return new Position(x, y, z, world);
	}

	public Location asLocation (World world) {
		return new Location(x, y, z, world, yaw, pitch);
	}
}
