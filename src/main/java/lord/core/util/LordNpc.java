package lord.core.util;

import beengine.entity.ai.helper.EntityBodyHelper;
import beengine.entity.ai.helper.EntityLookHelper;
import beengine.entity.any.EntityHuman;
import beengine.entity.util.Location;
import beengine.event.entity.EntityDamageByEntityEvent;
import beengine.event.entity.EntityDamageEvent;
import beengine.item.Item;
import beengine.minecraft.data.skin.SkinData;
import beengine.minecraft.generic.PacketInputStream;
import beengine.minecraft.generic.PacketOutputStream;
import beengine.minecraft.protocol.SkinCoder;
import beengine.minecraft.protocol.v465.SkinCoder_v465;
import beengine.nbt.NbtMap;
import beengine.player.Player;
import beengine.util.math.Vector3;
import lord.core.gamer.Gamer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LordNpc extends EntityHuman {

	private static final SkinCoder skinCoder = new SkinCoder_v465();
	public static final int DEFAULT_TAG_UPDATE_PERIOD = 10 * 20;

	public EntityLookHelper lookHelper;
	public EntityBodyHelper bodyHelper;
	protected BehaviorLookAtPlayer lookAtPlayer;

	protected boolean shouldLookAtPlayer;
	protected boolean lookingNow;

	protected int tagUpdateCounter = 0;
	protected int tagUpdatePeriod;

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

		hungerManager.setEnabled(false);

		setTagUpdatePeriod(DEFAULT_TAG_UPDATE_PERIOD);
	}

	public void setTagUpdatePeriod(int tagUpdatePeriod) {
		this.tagUpdatePeriod = tagUpdatePeriod;
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

			if (lookingNow) {
				lookAtPlayer.onTick();
			}

			lookHelper.onUpdate();
			bodyHelper.onUpdate();

			yaw = headYaw;
		}

		return super.onUpdate(currentTick) || lookingNow;
	}

	@Override
	protected boolean entityBaseTick(int tickDiff) {
		tagUpdateCounter -= tickDiff;
		if (tagUpdateCounter < 0) {
			tagUpdateCounter = tagUpdatePeriod;
			updateNameTag();
		}
		return super.entityBaseTick(tickDiff);
	}

	protected void updateNameTag () {

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

	public void onClick (Gamer gamer) {

	}

	@Override
	public final void attack(EntityDamageEvent source) {
		source.cancel();
		if (source instanceof EntityDamageByEntityEvent ev && ev.damager() instanceof Gamer gamer) {
			onClick(gamer);
		}
	}

	@Override
	public final boolean onFirstInteract(Player player, Item item, @Nullable Vector3 clickPos) {
		onClick((Gamer) player);
		return false;
	}
}
