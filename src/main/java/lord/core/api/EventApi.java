package lord.core.api;

import cn.nukkit.event.Event;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.level.*;
import cn.nukkit.event.player.*;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.event.potion.PotionApplyEvent;
import cn.nukkit.event.potion.PotionCollideEvent;
import cn.nukkit.event.server.*;
import cn.nukkit.event.vehicle.*;
import cn.nukkit.event.weather.LightningStrikeEvent;
import lombok.experimental.UtilityClass;
import lord.core.listener.service.EvH;
import lord.core.mgrbase.LordPlugin;

@UtilityClass
public class EventApi {
	
	/** Регистрирует прослушиватель события */
	public void register (EvH<? extends Event> listener) {
		register(listener, CoreApi.getCore());
	}
	
	public void register (EvH<? extends Event> listener, LordPlugin plugin) {
		CoreApi.getCore().pluginManager().registerEvents(listener, plugin);
	}
	// todo doc
	
	
	/* ======= BLOCK EVENTS ======= */
	
	/** Разрушение блока */
	public void onBlockBreak (EvH<BlockBreakEvent> listener) {
		register(listener);
	}
	
	/** Установка блока */
	public void onBlockPlace (EvH<BlockPlaceEvent> listener) {
		register(listener);
	}
	
	/** Открытие или закрытие двери */
	public void onDoorToggle (EvH<DoorToggleEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onSignChange (EvH<SignChangeEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onItemFrameDropItem (EvH<ItemFrameDropItemEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onBlockGrow (EvH<BlockGrowEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onBlockFall (EvH<BlockFallEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onBlockBurn (EvH<BlockBurnEvent> listener) {
		register(listener);
	}
	
	
	/* ======= ENTITY EVENTS ======= */
	
	/**  */
	public void onEntityDamageByEntity (EvH<EntityDamageByEntityEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityDamage (EvH<EntityDamageEvent> listener) {
		register( listener);
	}
	
	/**  */
	public void onProjectileLaunch (EvH<ProjectileLaunchEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onProjectileHit (EvH<ProjectileHitEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onItemSpawn (EvH<ItemSpawnEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onItemDespawn (EvH<ItemDespawnEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityVehicleExit (EvH<EntityVehicleExitEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityVehicleEnter (EvH<EntityVehicleEnterEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntitySpawn (EvH<EntitySpawnEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityShootBow (EvH<EntityShootBowEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityRegainHealth (EvH<EntityRegainHealthEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityMotion (EvH<EntityMotionEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityLevelChange (EvH<EntityLevelChangeEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityInteract (EvH<EntityInteractEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityDespawn (EvH<EntityDespawnEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityDeath (EvH<EntityDeathEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityDamageByBlock (EvH<EntityDamageByBlockEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityArmorChange (EvH<EntityArmorChangeEvent> listener) {
		register(listener);
	}
	
	
	/* ======= INVENTORY EVENTS ======= */
	
	/**  */
	public void onBrew (EvH<BrewEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onCraftItem (EvH<CraftItemEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEnchantItem (EvH<EnchantItemEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onFurnaceBurn (EvH<FurnaceBurnEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onFurnaceSmelt (EvH<FurnaceSmeltEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onInventoryClick (EvH<InventoryClickEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onInventoryClose (EvH<InventoryCloseEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onInventoryMoveItem (EvH<InventoryMoveItemEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onInventoryOpen (EvH<InventoryOpenEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onInventoryPickupItem (EvH<InventoryPickupItemEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onInventoryPickupArrow (EvH<InventoryPickupArrowEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onInventoryTransaction (EvH<InventoryTransactionEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onStartBrew (EvH<StartBrewEvent> listener) {
		register(listener);
	}
	
	
	/* ======= LEVEL EVENTS ======= */
	
	/**  */
	public void onChunkLoad (EvH<ChunkLoadEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onChunkUnload (EvH<ChunkUnloadEvent> listener) {
		register( listener);
	}
	
	/**  */
	public void onChunkPopulate (EvH<ChunkPopulateEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onLevelLoad (EvH<LevelLoadEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onLevelSave (EvH<LevelSaveEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onLevelUnload (EvH<LevelUnloadEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onThunderChange (EvH<ThunderChangeEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onWeatherChange (EvH<WeatherChangeEvent> listener) {
		register(listener);
	}
	
	
	/* ======= PLAYER EVENTS ======= */
	
	/**  */
	public void onPlayerDropItem (EvH<PlayerDropItemEvent> listener) {
		register( listener);
	}
	
	/**  */
	public void onPlayerJoin (EvH<PlayerJoinEvent> listener) {
		register( listener);
	}
	
	/**  */
	public void onPlayerMove (EvH<PlayerMoveEvent> listener) {
		register( listener);
	}
	
	/**  */
	public void onPlayerServerSettingsRequest (EvH<PlayerServerSettingsRequestEvent> listener) {
		register( listener);
	}
	
	/**  */
	public void onPlayerSettingsResponded (EvH<PlayerSettingsRespondedEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerSprint (EvH<PlayerToggleSprintEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerBedEnter (EvH<PlayerBedEnterEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerBedLeave (EvH<PlayerBedLeaveEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerBlockPick (EvH<PlayerBlockPickEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerBucketEmpty (EvH<PlayerBucketEmptyEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerBucketFill (EvH<PlayerBucketFillEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerChat (EvH<PlayerChatEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerCommandPreprocess (EvH<PlayerCommandPreprocessEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerCreation (EvH<PlayerCreationEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerDeath (EvH<PlayerDeathEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerEatFood (EvH<PlayerEatFoodEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerEditBook (EvH<PlayerEditBookEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerFoodLevelChange (EvH<PlayerFoodLevelChangeEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerGlassBottleFill (EvH<PlayerGlassBottleFillEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerInteractEntity (EvH<PlayerInteractEntityEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerInteract (EvH<PlayerInteractEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerItemConsume (EvH<PlayerItemConsumeEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerItemHeld (EvH<PlayerItemHeldEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerJump (EvH<PlayerJumpEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerLogin (EvH<PlayerLoginEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerPreLogin (EvH<PlayerPreLoginEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerQuit (EvH<PlayerQuitEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerRespawn (EvH<PlayerRespawnEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerTeleport (EvH<PlayerTeleportEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerToggleGlide (EvH<PlayerToggleGlideEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerToggleSneak (EvH<PlayerToggleSneakEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerToggleSprint (EvH<PlayerToggleSprintEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerToggleSwim (EvH<PlayerToggleSwimEvent> listener) {
		register(listener);
	}
	
	
	/* ======= PLUGIN EVENTS ======= */
	
	/**  */
	public void onPluginDisable (EvH<PluginDisableEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPluginEnable (EvH<PluginEnableEvent> listener) {
		register(listener);
	}
	
	
	/* ======= POTION EVENTS ======= */
	
	/**  */
	public void onPotionApply (EvH<PotionApplyEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPotionCollide (EvH<PotionCollideEvent> listener) {
		register(listener);
	}
	
	
	/* ======= SERVER EVENTS ======= */
	
	/**  */
	public void onBatchPackets (EvH<BatchPacketsEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onDataPacketReceive (EvH<DataPacketReceiveEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onDataPacketSend (EvH<DataPacketSendEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onPlayerDataSerialize (EvH<PlayerDataSerializeEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onQueryRegenerate (EvH<QueryRegenerateEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onRemoteServerCommand (EvH<RemoteServerCommandEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onServerCommand (EvH<ServerCommandEvent> listener) {
		register(listener);
	}
	
	
	/* ======= VEHICLE EVENTS ======= */
	
	/**  */
	public void onEntityEnterVehicle (EvH<EntityEnterVehicleEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onEntityExitVehicle (EvH<EntityExitVehicleEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onVehicleCreate (EvH<VehicleCreateEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onVehicleDamage (EvH<VehicleDamageEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onVehicleDestroy (EvH<VehicleDestroyEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onVehicleMove (EvH<VehicleMoveEvent> listener) {
		register(listener);
	}
	
	/**  */
	public void onVehicleUpdate (EvH<VehicleUpdateEvent> listener) {
		register(listener);
	}
	
	
	/* ======= WEATHER EVENTS ======= */
	
	/**  */
	public void onLightningStrike (EvH<LightningStrikeEvent> listener) {
		register(listener);
	}
	
}
