package lord.core.minigame;

import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.beengine.block.Block;
import dev.ghostlov3r.beengine.block.blocks.BlockSign;
import dev.ghostlov3r.beengine.event.EventListener;
import dev.ghostlov3r.beengine.event.EventPriority;
import dev.ghostlov3r.beengine.event.Priority;
import dev.ghostlov3r.beengine.event.block.BlockBreakEvent;
import dev.ghostlov3r.beengine.event.block.BlockPlaceEvent;
import dev.ghostlov3r.beengine.event.entity.EntityDamageByEntityEvent;
import dev.ghostlov3r.beengine.event.entity.EntityDamageEvent;
import dev.ghostlov3r.beengine.event.inventory.InventoryTransactionEvent;
import dev.ghostlov3r.beengine.event.player.*;
import dev.ghostlov3r.beengine.event.world.ChunkUnloadEvent;
import dev.ghostlov3r.beengine.player.Player;
import dev.ghostlov3r.beengine.score.Scoreboard;
import dev.ghostlov3r.beengine.utils.TextFormat;
import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.beengine.world.WorldManager;
import dev.ghostlov3r.math.Vector3;
import lord.core.minigame.arena.Arena;

import java.util.Map;

@Priority(EventPriority.HIGH)
public class MiniGameListener implements EventListener<MGGamer> {

	protected MiniGame manager;
	protected Map<Vector3, Integer> stateSigns;
	protected Class<? extends MGGamer> playerClass;

	public MiniGameListener (MiniGame manager, Map<Vector3, Integer> stateSigns, Class<? extends MGGamer> playerClass) {
		this.manager = manager;
		this.stateSigns = stateSigns;
		this.playerClass = playerClass;
	}

	@Override
	public void onPlayerCreation(PlayerCreationEvent event) {
		event.setActualClass(playerClass);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent<MGGamer> event) {
		MGGamer gamer = event.player();
		gamer.manager = manager;

		gamer.teleport(WorldManager.get().defaultWorld().getSpawnPosition());
		gamer.updateInventory();

		gamer.world().playersUnsafe().values().forEach(player -> {
			if (player instanceof MGGamer g) {
				if (g.shouldHidePlayers) {
					g.hidePlayer(gamer);
				}
			}
		});

		for (Player player : Server.onlinePlayers()) {
			((MGGamer)player).updateOnlineScoreInfo();
		}

		gamer.setScore(new Scoreboard(gamer));
		gamer.onLobbyJoin();
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent<MGGamer> event) {
		for (Player player : Server.onlinePlayers()) {
			((MGGamer)player).updateOnlineScoreInfo();
		}
	}

	@Override
	public void onInventoryTransaction(InventoryTransactionEvent event) {
		if (event.getTransaction().source() instanceof MGGamer gamer) {
			if (!gamer.inGame()) {
				event.cancel();
			}
		}
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		MGGamer gamer = null;

		if (event.entity() instanceof MGGamer g) {
			gamer = g;
		}
		else if (event instanceof EntityDamageByEntityEvent edbee) {
			if (edbee.damager() instanceof MGGamer damager) {
				gamer = damager;
			}
		}

		if (gamer != null && gamer.inGame()) {
			gamer.arena().onDamage(event);
		} else {
			event.cancel();
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent<MGGamer> event) {
		if (event.player().inGame()) {
			event.player().onBlockBreak(event);
			return;
		}
		event.cancel();
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent<MGGamer> event) {
		if (event.player().inGame()) {
			event.player().onBlockPlace(event);
			return;
		}
		event.cancel();
	}

	@Override
	public void onPlayerInteractBlock(PlayerInteractBlockEvent<MGGamer> event) {
		Block block = event.blockTouched();
		if (block instanceof BlockSign) {
			Arena arena = manager.getArenaBySign(block);
			if (arena != null) {
				arena.tryJoin(event.player());
			}
		}
	}

	@Override
	public void onChunkUnload(ChunkUnloadEvent event) {
		World world = event.getWorld();

		for (Arena arena : manager.arenas().values()) {
			if (arena.gameWorld() == world) {
				event.cancel();
				break;
			}
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent<MGGamer> event) {
		if (manager.config().randomJoinBlockEnabled) {
			if (event.player().arena() == null && event.player().world() == WorldManager.get().defaultWorld()) {
				if (event.isNotCancelled()) {
					if (event.player().world().getBlock(event.endPoint()).id() == manager.config().randomJoinBlockId) {
						Arena arena = manager.matchArenaForJoin();
						if (arena == null) {
							event.player().sendTip(TextFormat.RED + "Все арены заполнены!");
						} else {
							arena.tryJoin(event.player());
						}
					}
				}
			}
		}
	}

	/*@Override
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		if (event.isFlying()) {
			if (config().isLobbyDoubleJump()) {
				event.cancel();
				event.player().setMotion(event.player().directionVector().addY(0.5f));
				event.player().broadcastSound(Sound.of(WorldSoundEvent.SoundId.ARMOR_EQUIP_GENERIC), event.player().asList());
			}
		}
	}*/

}
