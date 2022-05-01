package lord.core;

import beengine.Server;
import beengine.block.Block;
import beengine.command.Command;
import beengine.command.CommandMap;
import beengine.entity.EntityFactory;
import beengine.event.EventListener;
import beengine.event.EventManager;
import beengine.event.block.BlockBreakEvent;
import beengine.event.block.BlockPlaceEvent;
import beengine.event.entity.EntityDamageEvent;
import beengine.event.inventory.InventoryTransactionEvent;
import beengine.event.player.*;
import beengine.event.server.QueryRegenerateEvent;
import beengine.item.LegacyStringToItemParser;
import beengine.player.Player;
import beengine.plugin.AbstractPlugin;
import beengine.scheduler.Scheduler;
import beengine.util.TextFormat;
import beengine.util.log.Logger;
import beengine.util.math.AxisAlignedBB;
import beengine.util.math.Vector3;
import beengine.world.World;
import beengine.world.generator.Generator;
import beengine.world.generator.VoidGenerator;
import lord.core.auth.Auth;
import lord.core.auth.RegisterData;
import lord.core.game.Teleport;
import lord.core.game.group.GroupMan;
import lord.core.game.rank.RankMan;
import lord.core.gamer.Gamer;
import lord.core.union.UnionHandler;
import lord.core.util.LordNpc;
import lord.core.util.LordNpcCommand;
import lord.core.util.MainGiftNpc;
import lord.core.util.UnionNpc;

import java.util.HashMap;
import java.util.Map;

// TODO
// Друзья
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
		Generator.register(VoidGenerator.class, "flat", true);
		registerEntities();
		Server.commandMap().register("npc", new LordNpcCommand());
		groups = new GroupMan();
		ranks = new RankMan();
		teleport  = new Teleport();
		auth = new Auth();
		unionHandler = new UnionHandler();
		Server.network().registerRawPacketHandler(unionHandler);
	}

	static class EditorCtx {
		Vector3 pos1;
		Vector3 pos2;
	}

	Map<Gamer, EditorCtx> editor = new HashMap<>();

	@Override
	public void onEnable () {
		if (config().isClearNukkitCommands()) {
			clearDefaults();
		}
		EventManager.get().register(this, this);

		Scheduler.delayedRepeat(1200, 1200, () -> {
			Server.unsafe().playerList().values().stream()
					.map(player -> (Gamer) player)
					.filter(Gamer::isAuthorized)
					.forEach(Gamer::incrementPlayedMinutes);
		});

		registerCommand("/pos1", (sender, args) -> {
			if (sender instanceof Gamer gamer) {
				EditorCtx ctx = editor.get(gamer); if (ctx == null) { ctx = new EditorCtx(); editor.put(gamer, ctx); } ctx.pos1 = gamer.toVector();
				editor.computeIfAbsent(gamer, __ -> new EditorCtx()).pos1 = gamer.toVector();
				gamer.sendMessage("Точка 1 на "+gamer.toVector());
			}
			return true;
		});

		registerCommand("/pos2", (sender, args) -> {
			if (sender instanceof Gamer gamer) {
				EditorCtx ctx = editor.get(gamer);
				if (ctx == null || ctx.pos1 == null) {
					gamer.sendMessage("Сначала нужна точка 1");
					return true;
				}
				ctx.pos2 = gamer.toVector();
				gamer.sendMessage("Точка 2 на "+gamer.toVector());
			}
			return true;
		});

		registerCommand("/set", (sender, args) -> {
			if (sender instanceof Gamer gamer) {
				EditorCtx ctx = editor.get(gamer);
				if (ctx == null || ctx.pos1 == null) {
					gamer.sendMessage("Сначала нужна точка 1");
					return true;
				}
				if (ctx.pos2 == null) {
					gamer.sendMessage("Сначала нужна точка 2");
					return true;
				}
				Block block = LegacyStringToItemParser.parse(args[0]).blockEquivalent();
				gamer.world().fill(new AxisAlignedBB(
					Math.min(ctx.pos1.x, ctx.pos2.x),
					Math.min(ctx.pos1.y, ctx.pos2.y),
					Math.min(ctx.pos1.z, ctx.pos2.z),
					Math.max(ctx.pos1.x, ctx.pos2.x),
					Math.max(ctx.pos1.y, ctx.pos2.y),
					Math.max(ctx.pos1.z, ctx.pos2.z)
				), block);
				gamer.sendMessage("Готово");
			}
			return true;
		});

		registerCommand("addexp", ((s, args) -> {
			Gamer target = s instanceof Gamer ? (Gamer) s : null;
			int cnt = Integer.parseInt(args[0]);
			if (args.length > 1) {
				target = (Gamer) Server.getPlayer(args[1]);
			}
			if (target == null) {
				s.sendMessage("Player not found");
			}
			else if (cnt < 1) {
				s.sendMessage("Count < 1");
			}
			else {
				target.addRankExp(cnt);
				s.sendMessage("Added "+cnt+" exp to "+target.name());
			}
			return true;
		}));
	}

	private void registerEntities () {
		EntityFactory.register(LordNpc.class, (loc, nbt) -> new LordNpc(loc, null), "LordNpc");
		EntityFactory.register(MainGiftNpc.class, (loc, nbt) -> new MainGiftNpc(loc, null), "MainGiftNpc");
		EntityFactory.register(UnionNpc.class, (loc, nbt) -> new UnionNpc(loc, null), "UnionNpc");
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
	public void onPlayerQuit(PlayerQuitEvent<Gamer> event) {
		event.setQuitMessage("");
		editor.remove(event.player());
		event.player().saveUnionData();
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
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent<Gamer> event) {
		Gamer gamer = event.player();
		if (!gamer.isAuthorized()) {
			event.cancel();

			if (!event.player().authChecked && !event.player().handlingPassword) {
				if (event.player().isRegistered()) {
					auth.handleLoginData(event.player(), event.message());
				} else {
					if (gamer.tempRegPassNoForm == null) {
						if (event.message().length() < 6) {
							gamer.sendMessage(TextFormat.RED + "Пароль должен быть не менее 6 знаков");
						}
						else if (event.message().contains(" ")) {
							gamer.sendMessage(TextFormat.RED + "Пароль не должен иметь пробел");
						}
						else {
							gamer.tempRegPassNoForm = event.message();
							gamer.sendMessage(">> §6Хороший пароль. Введите его еще раз");
						}
					}
					else {
						if (gamer.tempRegPassNoForm.equals(event.message())) {
							auth.handleRegisterData(event.player(), new RegisterData(event.message(), null, null));
						}
						else {
							gamer.sendMessage(TextFormat.RED+"Этот и первый пароль не совпали.");
						}
					}
				}
			}
		}
	}

	@Override
	public void onPlayerChat(PlayerChatEvent<Gamer> event) {
		Gamer gamer = event.player();
		if (gamer.isSpam(event.message())) {
			gamer.badMessages++;
			if (gamer.badMessages > 3) {
				gamer.badMessages = 0;
				gamer.sendMessage("За спам Вам запрещен чат на 5 минут");
			} else {
				gamer.sendMessage("Спам запрещен");
			}
			event.cancel();
		} else {
			event.setFormat(gamer.chatFormat());
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
		Gamer gamer = (Gamer) event.transaction().source();
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
					Scheduler.delay(1, () -> gamer.teleport(gamer.world().getSpawnPosition().addY(1), () -> {
						// Защита от почти невозможного кейса
						if (gamer.authChecked && gamer.world() == auth.world) {
							gamer.teleport(World.defaultWorld().getSpawnPosition());
						}
					}));
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

	@Override
	public void onQueryRegenerate(QueryRegenerateEvent event) {
		event.getQueryInfo().setListPlugins(false);
		event.getQueryInfo().setWorld("LORD");
		event.getQueryInfo().setPlayerList(Player.EMPTY_ARRAY);
	}
}