package lord.core.util;

import dev.ghostlov3r.beengine.entity.ai.helper.EntityBodyHelper;
import dev.ghostlov3r.beengine.entity.ai.helper.EntityLookHelper;
import dev.ghostlov3r.beengine.entity.any.EntityHuman;
import dev.ghostlov3r.beengine.entity.util.Location;
import dev.ghostlov3r.minecraft.data.skin.SkinData;
import dev.ghostlov3r.minecraft.generic.PacketInputStream;
import dev.ghostlov3r.minecraft.generic.PacketOutputStream;
import dev.ghostlov3r.minecraft.protocol.SkinCoder;
import dev.ghostlov3r.minecraft.protocol.v465.SkinCoder_v465;
import dev.ghostlov3r.nbt.NbtMap;
import lord.core.gamer.Gamer;

import java.util.function.Consumer;

public class LordNpc extends EntityHuman {

	private static final SkinCoder skinCoder = new SkinCoder_v465();

	public Consumer<Gamer> onHit;

	public EntityLookHelper lookHelper;
	public EntityBodyHelper bodyHelper;
	protected BehaviorLookAtPlayer lookAtPlayer;

	protected boolean shouldLookAtPlayer;
	protected boolean lookingNow;

	public LordNpc(Location location, SkinData skin) {
		super(location, skin);
	}

	@Override
	protected void initEntity() {
		super.initEntity();
		headYaw = yaw;
		lookHelper = new EntityLookHelper(this);
		bodyHelper = new EntityBodyHelper(this);
		lookAtPlayer = new BehaviorLookAtPlayer(this, 5);
	}

	public boolean shouldLookAtPlayer() {
		return shouldLookAtPlayer;
	}

	public void setShouldLookAtPlayer(boolean shouldLookAtPlayer) {
		this.shouldLookAtPlayer = shouldLookAtPlayer;
	}

	@Override
	public boolean onUpdate(int currentTick) {
		if (!this.spawned) {
			return false;
		}

		if (shouldLookAtPlayer()) {
			if (lookingNow) {
				if (!lookAtPlayer.canContinue()) {
					lookAtPlayer.onEnd();
					lookingNow = false;
				}
			} else {
				if (lookAtPlayer.canStart()) {
					lookAtPlayer.onStart();
					lookingNow = true;
				}
			}

			lookHelper.onUpdate();
			bodyHelper.onUpdate();
		}

		return super.onUpdate(currentTick) || lookingNow;
	}

	@Override
	public boolean hasMovementUpdate() {
		return Math.abs(this.yaw - this.lastLocation.yaw) > 0.0F
				|| Math.abs(this.pitch - this.lastLocation.pitch) > 0.0F
				|| Math.abs(this.headYaw - this.lastHeadYaw) > 0.0F;
	}

	@Override
	protected void checkBlockCollision() {
		// NOOP
	}

	@Override
	protected void tryChangeMovement() {
		// NOOP
	}

	@Override
	protected void onMovementUpdate() {
		// NOOP
	}

	@Override
	protected boolean doAirSupplyTick(int tickDiff) {
		return false;
	}

	@Override
	public boolean isInsideOfSolid() {
		return false;
	}

	@Override
	public void writeSaveData(NbtMap.Builder nbt) {
		super.writeSaveData(nbt);
		if (skin != null) {
			PacketOutputStream stream = new PacketOutputStream();
			skinCoder.write(skin, stream);
			nbt.setByteArray("LordSkin", stream.trimmedBuffer());
		}
		nbt.setBoolean("CustomNameAlwaysVisible", isNameTagAlwaysVisible());
		nbt.setBoolean("shouldLookAtPlayer", shouldLookAtPlayer());
	}

	@Override
	public void readSaveData(NbtMap nbt) {
		super.readSaveData(nbt);
		if (nbt.containsKey("LordSkin")) {
			PacketInputStream stream = new PacketInputStream(nbt.getByteArray("LordSkin"));
			skin = skinCoder.read(stream);
		}
		setNameTagAlwaysVisible(nbt.getBoolean("CustomNameAlwaysVisible", false));
		setShouldLookAtPlayer(nbt.getBoolean("shouldLookAtPlayer", false));
	}
}
