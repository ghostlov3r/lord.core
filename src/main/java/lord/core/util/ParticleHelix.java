package lord.core.util;

import dev.ghostlov3r.beengine.block.Position;
import dev.ghostlov3r.beengine.scheduler.Task;
import dev.ghostlov3r.beengine.world.Particle;

public class ParticleHelix extends Task {

	public Particle particle;
	public Position middle;
	public float radiusIncrement = 0;
	public float angleSpeed = 10;
	public float ySpeed = 0.1f;
	public float maxHeight = 5;

	public float angle = 0;
	public float radius = 3;
	public Position currentPos;

	public ParticleHelix (Particle particle, Position middle) {
		this.particle = particle;
		this.middle = middle;
		currentPos = new Position(0, middle.y, 0, middle.world());
	}

	@Override
	public void run() {
		calculateNextPos();
		currentPos.world().addParticle(currentPos, particle);
		radius += radiusIncrement;
		angle += angleSpeed;
		if (angle >= 360) {
			angle = 0;
		}
		currentPos.y += ySpeed;
		if (currentPos.y > (maxHeight + middle.y)) {
			cancel();
		}
	}

	private void calculateNextPos () {
		currentPos.z = middle.z + ((float) (radius * Math.sin(angle)));
		currentPos.x = middle.x + ((float) (radius * Math.cos(angle)));
	}
}
