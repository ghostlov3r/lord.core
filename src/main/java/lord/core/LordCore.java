package lord.core;

import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.beengine.command.Command;
import dev.ghostlov3r.beengine.command.CommandMap;
import dev.ghostlov3r.beengine.event.EventListener;
import dev.ghostlov3r.beengine.event.EventManager;
import dev.ghostlov3r.beengine.event.player.PlayerChatEvent;
import dev.ghostlov3r.beengine.event.player.PlayerCreationEvent;
import dev.ghostlov3r.beengine.event.player.PlayerJoinEvent;
import dev.ghostlov3r.beengine.event.player.PlayerToggleSneakEvent;
import dev.ghostlov3r.beengine.player.Player;
import dev.ghostlov3r.beengine.player.PlayerInfo;
import dev.ghostlov3r.beengine.plugin.AbstractPlugin;
import dev.ghostlov3r.log.Logger;
import dev.ghostlov3r.minecraft.MinecraftSession;
import dev.ghostlov3r.nbt.NbtMap;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lord.core.game.Teleport;
import lord.core.game.auth.Auth;
import lord.core.game.group.GroupMan;
import lord.core.game.rank.RankMan;
import lord.core.gamer.Gamer;

@Getter @Accessors(fluent = true)
public class LordCore extends AbstractPlugin<CoreConfig> implements EventListener<Gamer> {

	@Getter
	private static LordCore     instance;
	public  static Logger log;
	public Class<? extends Gamer> gamerClass = Gamer.class;
	
	private GroupMan      groupMan;
	private RankMan       rankMan;
	private Auth          auth;
	private Teleport      teleport;

	@Override
	public void onEnable () {
		instance = this;
		log = this.logger();

		this.groupMan  = new GroupMan();
		this.rankMan   = new RankMan();
		this.auth      = new Auth();
		this.teleport  = new Teleport();

		if (config().isClearNukkitCommands()) {
			clearDefaults();
		}

		EventManager.get().register(this, this);
	}
	
	@Override
	public void onDisable() {
	}
	
	/** Удаление всех дефолтных команд Nukkit */
	private void clearDefaults () {
		CommandMap commandMap = Server.commandMap();
		
		// Удаление дефолтных команд Nukkit
		for (Command command : commandMap.commands()) {
			if (config().vanillaCommands.contains(command.getName())) continue;
			command.unregister(commandMap);
		}
	}

	/** Сообщение авторизованным игрокам */
	public static void broadcast (String message) {
		for (Player player : Server.onlinePlayers()) {
			if (((Gamer)player).authorized()) player.sendMessage(message);
		}
	}

	@Override
	public void onPlayerCreation(PlayerCreationEvent event) {
		event.setActualClass(Gamer.class);
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent<Gamer> event) {
		event.setJoinMessage("");
		Gamer gamer = event.player();

		if (gamer.authorized()) {
			gamer.onSuccessAuth();
		} else {
			auth().requestAuth(gamer);
		}

		gamer.joinTime = System.currentTimeMillis();
	}

	@Override
	public void onPlayerToggleSneak(PlayerToggleSneakEvent<Gamer> event) {
		auth.onSneak(event);
	}

	@Override
	public void onPlayerChat(PlayerChatEvent<Gamer> event) {
		if (this.isSpam(event.message(), event.player())) {
			event.player().badMessages++;
			if (event.player().badMessages > 3) {
				event.player().badMessages = 0;
				event.player().sendMessage("За спам Вам запрещен чат на 5 минут");
			} else {
				event.player().sendMessage("Спам запрещен");
			}
			event.cancel();
		}
	}

	/** TRUE Если сообщение спам */
	private boolean isSpam (String message, Gamer gamer) {
		long current = System.currentTimeMillis();
		if ((current - gamer.lastChatTime) < 2000) {
			return true;
		}
		if (gamer.lastMessage.equals(message)) {
			return true;
		}
		gamer.lastChatTime = current;
		gamer.lastMessage = message;
		return false;
	}
}