package lord.core;

import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.beengine.command.Command;
import dev.ghostlov3r.beengine.command.CommandMap;
import dev.ghostlov3r.beengine.event.EventListener;
import dev.ghostlov3r.beengine.event.EventManager;
import dev.ghostlov3r.beengine.event.block.BlockBreakEvent;
import dev.ghostlov3r.beengine.event.block.BlockPlaceEvent;
import dev.ghostlov3r.beengine.event.entity.EntityDamageEvent;
import dev.ghostlov3r.beengine.event.inventory.InventoryTransactionEvent;
import dev.ghostlov3r.beengine.event.player.*;
import dev.ghostlov3r.beengine.plugin.AbstractPlugin;
import dev.ghostlov3r.beengine.scheduler.Scheduler;
import dev.ghostlov3r.beengine.world.generator.GeneratorManager;
import dev.ghostlov3r.beengine.world.generator.VoidGenerator;
import dev.ghostlov3r.log.Logger;
import lord.core.auth.Auth;
import lord.core.game.Teleport;
import lord.core.game.group.GroupMan;
import lord.core.game.rank.RankMan;
import lord.core.gamer.Gamer;
import lord.core.union.UnionHandler;

public class Lord extends AbstractPlugin<LordConfig> implements EventListener<Gamer> {

	public static Lord instance;
	public static Logger log;
	public static Class<? extends Gamer> gamerClass = Gamer.class;
	
	public static GroupMan groups;
	public static RankMan ranks;
	public static Teleport teleport;
	public static Auth auth;

	public static UnionHandler unionHandler;

	@Override
	protected void onLoad() {
		instance = this;
		log = this.logger();
		config().save();
		// Чтобы юзать флэт генератор потребуются соответствующие хаки, как и этот
		GeneratorManager.addGenerator(VoidGenerator.class, "flat", true);
		groups = new GroupMan();
		ranks = new RankMan();
		teleport  = new Teleport();
		auth = new Auth();
		unionHandler = new UnionHandler();
		Server.network().registerRawPacketHandler(unionHandler);
	}

	@Override
	public void onEnable () {
		if (config().isClearNukkitCommands()) {
			clearDefaults();
		}
		EventManager.get().register(this, this);
	}
	
	@Override
	public void onDisable() {
		unionHandler.shutdown();
		Server.network().unregisterRawPacketHandler(unionHandler);
	}

	@Override
	public void onPlayerCreation(PlayerCreationEvent event) {
		event.setActualClass(gamerClass);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent<Gamer> event) {
		event.setJoinMessage("");
		Gamer gamer = event.player();
		gamer.joinTime = System.currentTimeMillis();
		if (event.player().authChecked) {
			gamer.onSuccessAuth();
		} else {
			auth.requestAuth(event.player());
		}
	}

	@Override
	public void onPlayerToggleSneak(PlayerToggleSneakEvent<Gamer> event) {
		if (event.isSneaking()) {
			if (!event.player().authChecked && !event.player().handlingPassword) {
				if (event.player().isRegistered()) {
					auth.forms.login(event.player());
				} else {
					auth.forms.registerStart(event.player());
				}
			}
		}

		if (event.isSneaking() && event.player().onShift != null) {
			event.player().onShift.run();
		}
	}

	@Override
	public void onPlayerChat(PlayerChatEvent<Gamer> event) {
		Gamer gamer = event.player();
		if (!gamer.isAuthorized()) {
			event.cancel();
			return;
		}
		if (gamer.isSpam(event.message())) {
			gamer.badMessages++;
			if (gamer.badMessages > 3) {
				gamer.badMessages = 0;
				gamer.sendMessage("За спам Вам запрещен чат на 5 минут");
			} else {
				gamer.sendMessage("Спам запрещен");
			}
			event.cancel();
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent<Gamer> event) {
		if (!event.player().isAuthorized()) {
			event.cancel();
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent<Gamer> event) {
		if (!event.player().isAuthorized()) {
			event.cancel();
		}
	}

	@Override
	public void onInventoryTransaction(InventoryTransactionEvent event) {
		Gamer gamer = (Gamer) event.getTransaction().source();
		if (!gamer.isAuthorized()) {
			event.cancel();
		}
	}

	@Override
	public void onPlayerItemConsume(PlayerItemConsumeEvent<Gamer> event) {
		if (!event.player().isAuthorized()) {
			event.cancel();
		}
	}

	/** Удаление всех дефолтных команд Nukkit */
	private void clearDefaults () {
		CommandMap commandMap = Server.commandMap();

		// Удаление дефолтных команд Nukkit
		for (Command command : commandMap.commands()) {
			if (config().vanillaCommands.contains(command.getName())) continue;
			//command.unregister(commandMap); // TODO
		}
	}

	/** Сообщение авторизованным игрокам */
	public static void broadcast (String message) {
		Server.broadcastMessage(message);
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.entity() instanceof Gamer gamer) {
			if (!gamer.isAuthorized()) {
				event.cancel();
				if (event.cause() == EntityDamageEvent.Cause.VOID) {
					Scheduler.delay(1, () -> gamer.teleport(gamer.world().getSpawnPosition().addY(1)));
				}
			}
		}
	}

	@Override
	public void onPlayerDataSave(PlayerDataSaveEvent event) {
		Gamer gamer = (Gamer) event.player();
		if (gamer != null) {
			if (!gamer.isAuthorized()) {
				event.cancel();
			}
		}
	}
}