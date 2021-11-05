package lord.core.game.achieve;

import dev.ghostlov3r.beengine.utils.config.Config;
import dev.ghostlov3r.common.DiskMap;
import dev.ghostlov3r.math.FRand;
import lord.core.LordCore;

/**
 * Менеджер достижений игроков
 * @author ghostlov3r
 */
public class AchieveMan extends DiskMap<String, Achieve> {

	public AchieveMan () {
		super(LordCore.instance().dataPath().resolve("achieves"), Achieve.class);
		loadAll();
	}
	
	public Achieve create (String name, String text) {
		var ach = new Achieve(this, String.valueOf(FRand.nextInt()));
		ach.achieveName = name;
		ach.achieveText = text;
		return ach;
	}
	
}
