package lord.core.game.warp;

import beengine.Server;
import beengine.entity.util.Location;
import beengine.player.Player;
import beengine.util.DiskEntry;
import beengine.util.DiskMap;
import beengine.world.World;
import lombok.Getter;
import java.util.List;

@Getter
public class Warp extends DiskEntry<String> {

	protected Location location;

	protected String worldName;
	
	protected boolean opened;
	protected String ownerName;
	protected List<String> whiteList;

	public Warp(DiskMap<String, ?> map, String key) {
		super(map, key);
	}
	
	/**
	 * Телепортирует игрока на позицию
	 * @return Успешно ли все с миром, но не успешный телепорт.
	 */
	public boolean teleport (Player gamer) {
		World.load(worldName).onResolve(p -> {
			try {
				Location loc = location.toLocation();
				loc.setWorld(loc.world());
				gamer.teleport(location);
			}
			catch (Exception e) {
				Server.logger().logException(e);
			}
		});
		return true;
	}
	
}
