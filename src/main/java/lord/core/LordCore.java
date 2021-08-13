package lord.core;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.event.Listener;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.scheduler.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.var;
import lord.core.api.CoreApi;
import lord.core.api.EventApi;
import lord.core.api.TaskApi;
import lord.core.game.Teleport;
import lord.core.game.auth.Auth;
import lord.core.game.group.GroupMan;
import lord.core.game.rank.RankMan;
import lord.core.game.sanction.Sanction;
import lord.core.gamer.Gamer;
import lord.core.gamer.GamerMan;
import lord.core.listener.PlayerCreationListener;
import lord.core.listener.PlayerJoinListener;
import lord.core.listener.PlayerQuitListener;
import lord.core.mgrbase.LordPlugin;
import lord.core.scoreboard.ScoreManager;
import lord.core.util.SaveTask;
import lord.core.util.Util;
import lord.core.util.file.AdvFile;
import lord.core.util.file.AdvFolder;

import java.util.ArrayList;
import java.util.List;

@Getter @Accessors(fluent = true)
public class LordCore extends LordPlugin implements Listener {
	
	public static final String CONFIG_FILE_NAME = "config";
	
	@Getter
	private static LordCore     instance;
	public  static Server       server;
	public  static PluginLogger log;
	
	/** Менеджер событий      */  private PluginManager pluginManager;
	/** Менеджер групп        */  private GroupMan      groupMan;
	/** Менеджер рангов       */  private RankMan       rankMan;
	/** Менеджер скорбордов   */  private ScoreManager  scoreMan;
	/** Банлист               */  private Sanction      sanctions;
	/** Авторизация           */  private Auth          auth;
	/** Основные конфигурации */  private CoreConfig    config;
	/** saveTask              */  private SaveTask      saveTask;
	/**                       */  private Teleport      teleport;
	
	/** Менеджер данных игроков */
	@Setter private GamerMan gamerMan;
	
	public void invokeOnEnable (Class clazz) {
		NukkitRandom
		Util.invoke(clazz, "onEnable", this);
	}
	
	@Override
	public void onEnable () {
		instance = this;
		server = this.getServer();
		log = this.getLogger();
		
		gamerMan = new GamerMan.TestMgr();
		
		for (Class clazz : new Class[] {
			CoreApi   .class,
			TaskApi   .class,
			AdvFile   .class,
			AdvFolder .class,
			Gamer     .class
		}) {
			invokeOnEnable(clazz);
		}
		
		this.loadCoreCfg();
		
		this.pluginManager = server.getPluginManager();
		this.groupMan  = new GroupMan();
		this.rankMan   = new RankMan();
		this.scoreMan  = new ScoreManager(this);
		this.sanctions = new Sanction();
		this.auth      = new Auth();
		this.teleport  = new Teleport();
		
		int savePeriod = this.config.getSavePeriodSec();
		TaskApi.delayedRepeat(savePeriod, savePeriod, this.saveTask = new SaveTask());
		
		int minutes = this.config.getShutdownDelayMin();
		TaskApi.delay(minutes * 60, new ShutdownTask(this));
		log.info(minutes + " min before Shutdown");
		
		if (this.config.isPacketLoggerEnabled()) {
			EventApi.onDataPacketReceive(event -> {
				log.info("SENT PACKET: " + event.getPacket().getClass().getSimpleName());
			});
			EventApi.onDataPacketSend(event -> {
				log.info("SENT PACKET: " + event.getPacket().getClass().getSimpleName());
			});
		}
		
		if (config.isClearNukkitCommands()) {
			clearDefaults();
		}
		
		EventApi.register(new PacketHandler());
		EventApi.register(new PlayerCreationListener());
		EventApi.register(new PlayerJoinListener());
		EventApi.register(new PlayerQuitListener());
	}
	
	@Override
	public void onDisable() {
		this.processSaver();
	}
	
	private void loadCoreCfg () {
		var cfgFile = this.getFolder().getFile(CONFIG_FILE_NAME, AdvFile.JSON);
		if (cfgFile.exists()) {
			this.config = cfgFile.readJson(CoreConfig.class);
		} else {
			this.config = new CoreConfig();
			this.config.savePretty(cfgFile);
		}
	}
	
	/** Выполняет сохранение из SaveTask */
	public void processSaver () {
		this.saveTask.saveAll();
	}
	
	/** Удаление всех дефолтных команд Nukkit */
	private void clearDefaults () {
		SimpleCommandMap commandMap = server.getCommandMap();
		List<String> skipDeleting = new ArrayList<>();
		List<String> vanillaNames = new ArrayList<>(commandMap.getCommands().keySet());
		
		AdvFile cmdsFile = this.getFolder().getFile("vanilla_commands.txt");
		if (!cmdsFile.exists()) {
			cmdsFile.create();
			
			String[] defaultContent = { "example" };
			cmdsFile.writeJson(defaultContent);
		}
		
		// Формирование списка команд, не подлежащих удалению
		try {
			String[] skips = cmdsFile.readJson(String[].class);
			for (String skip : skips) {
				skip = skip.trim();
				
				if (System.lineSeparator().equals(skip)) continue;
				if ("".equals(skip)) continue;
				if (skip.isEmpty()) continue;
				
				skipDeleting.add(skip);
			}
		} catch (Exception e) {
			log.error("ERROR :: Cannot read vanilla_commands");
			e.printStackTrace();
		}
		
		// Удаление дефолтных команд Nukkit
		for (String name : vanillaNames) {
			if (skipDeleting.contains(name)) continue;
			commandMap.getCommand(name).unregister(commandMap);
		}
		LordCore.log.info("After deleting default commands registry size is " + commandMap.getCommands().size());
	}
	
	@RequiredArgsConstructor
	private static class ShutdownTask extends Task {
		private final LordCore core;
		@Override
		public void onRun (int i)
		{
			for (Player player : Server.getInstance().getOnlinePlayers().values()) {
				player.sendTip("До перезапуска 2 минуты");
				player.sendMessage("Перезапуск сервера через 2 минуты.");
			}
			TaskApi.delayedRepeat(100, 5, new ShutdownTimer(core));
		}
	}
	
	// будет выполнен 6 раз. На 6 shutdown. Стартает каждые 5 сек
	@RequiredArgsConstructor
	private static class ShutdownTimer extends Task {
		private int counter = 0;
		public final LordCore core;
		@Override
		public void onRun (int tick) {
			this.counter++;
			
			switch (this.counter) {
				case 1: // 30 сек
					this.core.getServer().getOnlinePlayers().forEach((uuid, player) ->
						player.sendTitle("", "До перезапуска 30 секунд", 10, 40, 20));
					break;
				case 4: // 15 сек
					Server.getInstance().broadcastMessage(this.core.config().getPrefix() + "Перезапуск сервера через 15 секунд.");
					break;
				case 6: // 5 сек
					// запрет на вход сделать
					this.core.getServer().getOnlinePlayers().forEach((uuid, player) ->
						player.sendTitle("", "Перезапуск сервера...", 10, 60, 20));
					break;
				case 7:
					this.core.getServer().getOnlinePlayers().forEach((uuid, player) ->
						player.close("ПЕРЕЗАПУСК", this.core.config().getPrefix() + " Сейчас перезапускается. " + System.lineSeparator() + "Вы сможете снова играть через 5 секунд."));
					LordCore.log.info("Игроки исключены, сервер перезаупскается");
					this.core.getServer().shutdown();
					break;
			}
			
		}
	}
	
}