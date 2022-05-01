package lord.core.util;

import beengine.entity.obj.ItemEntity;
import beengine.entity.util.Location;
import beengine.item.Item;
import beengine.item.Items;
import beengine.minecraft.data.skin.SkinData;
import beengine.nbt.NbtMap;
import beengine.scheduler.Scheduler;
import beengine.util.TextFormat;
import beengine.util.math.FRand;
import beengine.util.math.Vector3;
import beengine.world.Particle;
import beengine.world.Sound;
import lord.core.gamer.Gamer;

public class MainGiftNpc extends LordNpc {

	static int THROW_COUNTER = 20 * 15;

	int itemThrowCounter = THROW_COUNTER;

	public MainGiftNpc(Location location, SkinData skin) {
		super(location, skin);
	}

	@Override
	protected void initEntity() {
		super.initEntity();

		setShouldLookAtPlayer(true);
		setNameTag(TextFormat.GREEN+"Получай награду!");
	}

	@Override
	public void readSaveData(NbtMap nbt) {
		super.readSaveData(nbt);
		setShouldLookAtPlayer(true);
		setNameTagVisible();
		setNameTagAlwaysVisible();
	}

	@Override
	protected boolean entityBaseTick (int tickDiff) {
		itemThrowCounter -= tickDiff;
		if (itemThrowCounter < 0) {
			itemThrowCounter = THROW_COUNTER;
			throwItems();
		}
		return super.entityBaseTick(tickDiff);
	}

	private static Item[] ITEMS = {
		Items.EMERALD(),
		Items.EMERALD(),
		Items.DIAMOND(),
		Items.DIAMOND(),
		Items.GOLD_INGOT(),
		Items.GOLD_INGOT(),
		Items.IRON_INGOT()
	};

	private void throwItems () {
		if (world.unsafe().players().isEmpty()) {
			return;
		}
		for (int i = 0; i < ITEMS.length; i++) {
			Item item = ITEMS[i].clone();

			final int _i = i;
			Scheduler.delay(i * 10, () -> {
				ItemEntity entity = world.dropItem(this.addY(1), item, new Vector3(FRand.nextSignedFloat(random) * 0.3f, 0.8f, FRand.nextSignedFloat(random) * 0.3f));
				entity.setCanBeMerged(false);
				entity.setDespawnDelay(THROW_COUNTER - (_i * 10));
				entity.setPickupDelay(ItemEntity.NEVER_DESPAWN);
				broadcastSound(Sound.POP);
			});
		}

		Scheduler.delay((ITEMS.length * 10) + 5, () -> {
			broadcastSound(Sound.XP_COLLECT);
			float y = this.y + 0.3f;
			for (int i = 0; i < 3; i++) {
				world.addParticle(FRand.nextSignedFloat(random) * 1.5f + this.x, y, FRand.nextSignedFloat(random) * 1.5f + this.z, Particle.LAVA);
			}
		});
	}

	@Override
	public void onClick (Gamer gamer) {
		gamer.showGiftMenu();
	}
}
