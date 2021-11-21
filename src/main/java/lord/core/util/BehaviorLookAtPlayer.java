package lord.core.util;

import dev.ghostlov3r.beengine.entity.Entity;
import dev.ghostlov3r.beengine.entity.EntityLiving;
import dev.ghostlov3r.beengine.player.Player;
import dev.ghostlov3r.math.FMath;

public class BehaviorLookAtPlayer {

	protected float $lookDistance;
	protected Player $nearestEntity;
	protected int $lookTime = 0;
	protected LordNpc mob;

	public BehaviorLookAtPlayer(LordNpc $mob) {
		this($mob, 6);
	}

	public BehaviorLookAtPlayer (LordNpc $mob, float $lookDistance){
		this.mob = $mob;
		this.$lookDistance = $lookDistance;
	}

	public boolean canStart() {
		if(mob.random.nextFloat() < 0.02f){
			Player target = mob.world().getNearestEntity(mob, $lookDistance, Player.class);

			if(target != null){
				$nearestEntity = target;

				return true;
			}
		}

		return false;
	}

	public void onStart() {
		$lookTime = 40 + mob.random.nextInt(40);
	}

	public boolean canContinue() {
		return !($nearestEntity.isAlive() ? false : ((mob.distanceSquared($nearestEntity) > FMath.square($lookDistance)) ? false : ($lookTime > 0)));
	}

	public void onTick() {
		mob.lookHelper.setLookPositionWithEntity($nearestEntity, 10, 30);
		$lookTime--;
	}

	public void onEnd() {
		$nearestEntity = null;
	}
}
