package lord.core.minigame;

import dev.ghostlov3r.beengine.entity.any.EntityHuman;
import dev.ghostlov3r.beengine.utils.TextFormat;

public class DeadGamer extends EntityHuman {

	public DeadGamer(MGGamer gamer) {
		super(gamer, gamer.skin());
		maxDeadTicks = Integer.MAX_VALUE;
		setNameTag(TextFormat.GRAY + gamer.name() + TextFormat.RED+" (Умер)");
		spawn();
		kill();
		startDeathAnimation();
	}
}
