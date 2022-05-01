package lord.core.game.achieve;

import beengine.util.DiskMap;
import beengine.util.math.FRand;
import lord.core.Lord;

/**
 * Менеджер достижений игроков
 * @author ghostlov3r
 */
public class AchieveMan extends DiskMap<String, Achieve> {

	public AchieveMan () {
		super(Lord.instance.dataPath().resolve("achieves"), Achieve.class);
		loadAll();
	}
	
	public Achieve create (String name, String text) {
		var ach = new Achieve(this, String.valueOf(FRand.nextInt()));
		ach.achieveName = name;
		ach.achieveText = text;
		return ach;
	}
	
}
