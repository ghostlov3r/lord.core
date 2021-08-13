package lord.core.game.warp;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import lombok.Getter;
import lombok.var;
import lord.core.util.json.JsonSkip;
import lord.core.mgrbase.entry.LordEntryF;

import java.util.List;

@Getter
public class Warp extends LordEntryF<WarpMan> {
	
	/** Перед телепортацией нужно проверять location.level на null */
	@JsonSkip
	protected Location location;
	
	protected double x;
	protected double y;
	protected double z;
	protected double yaw;
	protected double pitch;
	protected String worldName;
	
	protected boolean opened;
	protected String ownerName;
	protected List<String> whiteList;
	
	@Override
	public void finup (String name, WarpMan manager) {
		super.finup(name, manager);
		location = new Location(x, y, z, yaw, pitch, null);
	}
	
	/**
	 * Пробудет загрузить и установить мир в Location
	 * @return Успех
	 */
	private boolean initLevel () {
		var server = getManager().getCore().getServer();
		if (!server.isLevelLoaded(worldName)) {
			server.loadLevel(worldName);
		}
		Level level = server.getLevelByName(worldName);
		if (level == null) return false;
		location.level = level;
		return true;
	}
	
	/**
	 * Телепортирует игрока на позицию
	 * @return Успешно ли все с миром, но не успешный телепорт.
	 */
	public boolean teleport (Player gamer) {
		if (location.level == null) {
			if (!initLevel()) return false;
		}
		gamer.teleport(location);
		return true;
	}
	
}
