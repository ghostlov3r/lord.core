package lord.core.util;

import beengine.player.Player;
import beengine.util.math.FMath;

public class BehaviorLookAtPlayer {

	protected float lookDistance;
	protected Player nearestPlayer;
	protected int lookTime = 0;
	protected LordNpc mob;

	public BehaviorLookAtPlayer(LordNpc mob) {
		this(mob, 6);
	}

	public BehaviorLookAtPlayer (LordNpc mob, float lookDistance){
		this.mob = mob;
		this.lookDistance = lookDistance;
	}

	public boolean canStart() {
		if(mob.random.nextFloat() < 0.05f){
			Player target = mob.world().getNearestEntity(mob, lookDistance, Player.class);

			if(target != null){
				nearestPlayer = target;

				return true;
			}
		}

		return false;
	}

	public void onStart() {
		lookTime = 50 + mob.random.nextInt(40);
	}

	public boolean canContinue() {
		return lookTime > 0
				&& nearestPlayer.isAlive()
				&& nearestPlayer.isSpawned()
				&& (mob.distanceSquared(nearestPlayer) <= FMath.square(lookDistance));
	}

	public void onTick() {
		mob.lookHelper.setLookPositionWithEntity(nearestPlayer, 10, 30);
		lookTime--;
	}

	public void onEnd() {
		nearestPlayer = null;
	}
}
