package lord.core.game.region;

import dev.ghostlov3r.beengine.Beengine;
import dev.ghostlov3r.beengine.block.blocks.BlockDoor;
import dev.ghostlov3r.beengine.entity.Entity;
import dev.ghostlov3r.beengine.event.EventListener;
import dev.ghostlov3r.beengine.event.EventManager;
import dev.ghostlov3r.beengine.event.block.BlockBreakEvent;
import dev.ghostlov3r.beengine.event.block.BlockPlaceEvent;
import dev.ghostlov3r.beengine.event.entity.EntityDamageByEntityEvent;
import dev.ghostlov3r.beengine.event.player.PlayerInteractBlockEvent;
import dev.ghostlov3r.beengine.event.world.ChunkLoadEvent;
import dev.ghostlov3r.beengine.event.world.ChunkUnloadEvent;
import dev.ghostlov3r.beengine.world.Chunk;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import lord.core.LordCore;
import lord.core.game.region.store.IRegionStore;
import lord.core.game.region.store.RegionStore;

import java.nio.file.Files;
import java.nio.file.Path;

public class Regions {
	
	/** Папка regions */
	public final Path FOLDER;
	
	@Setter
	private RegionActions actions;
	
	private IRegionStore store;
	
	private boolean loaderEnabled;
	
	private boolean logEnabled;
	
	@SneakyThrows
	public Regions (boolean withLoader) {
		this.FOLDER = LordCore.instance().dataPath().resolve("regions");
		Files.createDirectories(FOLDER);
		
		this.actions = new RegionActions();
		this.store = new RegionStore();
		this.logEnabled = true;
		
		if (withLoader) {
			this.enableLoader();
		}


		EventManager.get().register(LordCore.instance(), new EventListener() {
			@Override
			public void onBlockPlace(BlockPlaceEvent event) {
				if (!actions.onBuild()) { // todo
					event.cancel();
				}

				/*
				if (!Region.playerCanBuild(event.getPlayer().getName(), event.getBlock())) {
			event.setCancelled(true);
			event.getPlayer().sendTip("Регион под защитой");
		}
				 */
			}

			@Override
			public void onBlockBreak(BlockBreakEvent event) {
				if (!actions.onBuild()) { // todo
					event.cancel();
				}

				/*
				Player player = event.getPlayer();
		String name = player.getName();
		if (!Region.playerCanBuild(name, event.getBlock())) {
			event.setCancelled(true);
			player.sendTip("РЕГИОН ПОД ЗАЩИТОЙ");
			return;
		}
		int reward = 0;
		int id = event.getBlock().getId();
		if (Block.COAL_ORE == id)    reward = 1;
		if (Block.IRON_ORE == id)    reward = 2;
		if (Block.GOLD_ORE == id)    reward = 3;
		if (Block.DIAMOND_ORE == id) reward = 4;
		if (Block.EMERALD_ORE == id) reward = 5;

		if (reward != 0) {
			Gamer.get(name).addMoney(reward);
			player.sendTip("+ " + reward + " Koins");
		}
				 */
			}

			@Override
			public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
				Entity damager = event.damager();
				Entity entity = event.entity();

				if (!actions.onDamage()) { // todo
					event.cancel();
				}
			}

			@Override
			public void onPlayerInteractBlock(PlayerInteractBlockEvent event) {
				if (event.blockTouched() instanceof BlockDoor) {
					if (!actions.onDoor()) { // todo
						event.cancel();
					}
				}
			}

			@Override
			public void onChunkLoad(ChunkLoadEvent event) {
				if (loaderEnabled) {
					Chunk chunk = event.chunk();
					tryLoadRegion(chunk.x(), chunk.z());
				}
			}

			@Override
			public void onChunkUnload(ChunkUnloadEvent event) {
				if (loaderEnabled) {
					Chunk chunk = event.chunk();
					tryUnloadRegion(chunk.x(), chunk.z());
				}
			}
		});
	}

	/** Включает загрузку с диска, сохранение и выгрузку */
	public void enableLoader () {
		loaderEnabled = true;
	}
	
	/** Возвращает AdvFile региона */
	public Path regionFile (int x, int z) {
		return this.FOLDER.resolve(x + "_" + z + ".json");
	}
	
	/** Загружает регион в Store, вернет Null если файла нет. */
	@SneakyThrows
	public Region tryLoadRegion (int x, int z) {
		Path regionFile = this.regionFile(x, z);
		if (!Files.exists(regionFile)) {
			return null;
		}
		Region region = Beengine.JSON_MAPPER.readValue(regionFile.toFile(), Region.class);

		region.x = x;
		region.z = z;
		this.store.putRegionToMap(region);
		if (this.logEnabled) LordCore.log.info("Loaded region " + region.x + "/" + region.z);
		return region;
	}
	
	/** Пробует выгрузить неслужебный регион */
	public void tryUnloadRegion (int x, int z) {
		Region region = this.store.getRegion(x, z);
		if (region != null && !region.service) {
			region.save();
			this.store.removeRegionFromMap(region.x, region.z);
			if (this.logEnabled) LordCore.log.info("Unloaded region " + region.x + "/" + region.z);
		}
	}
	
	/* static class InventoryOpenListener implements LordListener {
		@Override @EventHandler
		public void handle (Event ev) {
			InventoryOpenEvent event = (InventoryOpenEvent) ev;
			Inventory inv = event.getInventory();
			
			if (inv.getType() == InventoryType.CHEST) {
				ChestInventory inventory = (ChestInventory) event.getInventory();
				inventory.getHolder();
			}
			if (inv.getType() == InventoryType.DOUBLE_CHEST) {
				DoubleChestInventory inventory = (ChestInventory) event.getInventory();
				inventory.getHolde;
				BlockChest
			}
			
			Region region = getRegion(event.getBlock());
			if (region == null) {
				if (!doorAbsent.apply((LordPlayer) event.getPlayer(), event)) {
					event.setCancelled();
					return;
				}
			}
			if (!doorPresent.apply((LordPlayer) event.getPlayer(), region)) {
				event.setCancelled();
			}
		}
	}*/
	
}
