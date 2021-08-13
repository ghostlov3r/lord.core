package lord.core.game.region;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.level.ChunkLoadEvent;
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.level.format.FullChunk;
import lombok.Setter;
import lombok.val;
import lord.core.LordCore;
import lord.core.game.region.store.IRegionStore;
import lord.core.game.region.store.RegionStore;
import lord.core.api.EventApi;
import lord.core.util.file.AdvFile;
import lord.core.util.file.AdvFolder;

public class Regions {
	
	/** Папка regions */
	public final AdvFolder FOLDER;
	
	@Setter
	private RegionActions actions;
	
	private IRegionStore store;
	
	private boolean loaderEnabled;
	
	private boolean logEnabled;
	
	public Regions (boolean withLoader) {
		this.FOLDER = LordCore.FOLDER.getChild("regions");
		this.FOLDER.mkdirIfNot();
		
		this.actions = new RegionActions();
		this.store = new RegionStore();
		this.logEnabled = true;
		
		if (withLoader) {
			this.enableLoader();
		}
		
		this.registerListeners();
	}
	
	private void registerListeners () {
		EventApi.onBlockPlace(ev -> { val event = (BlockPlaceEvent) ev;
			if (!this.actions.onBuild()) { // todo
				event.setCancelled();
			}
		});
		EventApi.onBlockBreak(ev -> { val event = (BlockBreakEvent) ev;
			if (!this.actions.onBuild()) { // todo
				event.setCancelled();
			}
		});
		EventApi.onEntityDamageByEntity(ev -> { val event = (EntityDamageByEntityEvent) ev;
			Entity damager = event.getDamager();
			Entity entity = event.getEntity();
			
			if (!this.actions.onDamage()) { // todo
				event.setCancelled();
			}
		});
		EventApi.onDoorToggle(ev -> { val event = (DoorToggleEvent) ev;
			Region region = this.store.getRegion(event.getBlock());
			
			if (!this.actions.onDoor()) { // todo
				event.setCancelled();
			}
		});
		
		// TODO chest
	}
	
	/** Включает загрузку с диска, сохранение и выгрузку */
	public void enableLoader () {
		if (this.loaderEnabled) {
			return;
		}
		
		EventApi.onChunkLoad(ev -> { val event = (ChunkLoadEvent) ev;
			FullChunk chunk = event.getChunk();
			this.tryLoadRegion(chunk.getX(), chunk.getZ());
		});
		EventApi.onChunkUnload(ev -> { val event = (ChunkUnloadEvent) ev;
			FullChunk chunk = event.getChunk();
			this.tryUnloadRegion(chunk.getX(), chunk.getZ());
		});
		
		this.loaderEnabled = true;
	}
	
	/** Возвращает AdvFile региона */
	public AdvFile regionFile (int x, int z) {
		return this.FOLDER.getFile(x + "_" + z + ".json");
	}
	
	/** Загружает регион в Store, вернет Null если файла нет. */
	public Region tryLoadRegion (int x, int z) {
		AdvFile regionFile = this.regionFile(x, z);
		if (!regionFile.exists()) {
			return null;
		}
		Region region = regionFile.readJson(Region.class);
		if (region != null) {
			region.x = x;
			region.z = z;
			this.store.putRegionToMap(region);
			if (this.logEnabled) LordCore.log.info("Loaded region " + region.x + "/" + region.z);
		}
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
