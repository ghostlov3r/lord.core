package lord.core.game.warp;

import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.beengine.entity.util.Location;
import dev.ghostlov3r.beengine.player.Player;
import dev.ghostlov3r.beengine.world.WorldManager;
import dev.ghostlov3r.common.DiskEntry;
import dev.ghostlov3r.common.DiskMap;
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
		WorldManager.get().loadWorld(worldName).onResolve(p -> {
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
