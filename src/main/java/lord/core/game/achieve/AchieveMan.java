package lord.core.game.achieve;

import lombok.var;
import lord.core.LordCore;
import lord.core.mgrbase.manager.LordManF;

/**
 * Менеджер достижений игроков
 * @author ghostlov3r
 */
public class AchieveMan extends LordManF<Achieve, LordCore> {
	
	public AchieveMan () {
		prettyJson();
		loadAll();
	}
	
	public Achieve create (String name, String text) {
		var ach = new Achieve();
		ach.achieveName = name;
		ach.achieveText = text;
		ach.finup(nextIntName(), this);
		return ach;
	}
	
}
