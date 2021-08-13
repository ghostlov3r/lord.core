package lord.core.minigame.arena;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import lord.core.api.TaskApi;
import lord.core.mgrbase.entry.LordEntryF;
import lord.core.minigame.LordArenaMan;
import lord.core.minigame.MiniGameConfig;
import lord.core.minigame.mggamer.MGGamer;
import lord.core.util.Util;
import lord.core.util.file.AdvFolder;
import lord.core.util.json.JsonSkip;
import lord.core.util.logger.ILordLogger;
import lord.core.util.logger.LordLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Элемент менеджера LordArenaMan
 * Представляет собой арену какой-либо миниигры
 *
 * @param <ArenaMan> Тип менеджера арен
 * @param <Team> Тип команд
 * @author ghostlov3r
 */
@Getter
public abstract class LordArena<ArenaMan extends LordArenaMan, Team extends LordArenaTeam, G extends MGGamer, WinData extends ArenaWinData>
	extends LordEntryF<ArenaMan> {
	
	@JsonSkip  protected final ILordLogger logger  = LordLogger.get("Arena " + name);
	@JsonSkip  protected final Server      server  = Server.getInstance();
	
	@JsonSkip  protected LordArenaTicker      ticker = null;
	@JsonSkip  private   ActualArenaDurations actualDurations;
	
	@JsonSkip @Setter   protected ArenaState state = ArenaState.RELOAD;
	@JsonSkip @Nullable private   Level      world = null;
	
	@JsonSkip  private    boolean     solo;
	@JsonSkip  private    int         gamersCount;
	@JsonSkip  private    int         maxPlayers;
	@JsonSkip  private    WinData     winData; // Не Null после победы или конца таймера
	
	/* ====================================== JSON =========================================== */
	
	private String            worldName;
	private int               teamSlots; // Если 1, арена сольная
	private Map<String, Team> teams;
	private ArenaDurations    durations;
	
	/** Мин. число чел. для отсчета
	 * Если арена соло, то общее число; если командная, то чел. в команде */
	private int minPlayers;
	
	/* ======================================================================================= */
	
	/** Инициализатор */
	@Override
	@SuppressWarnings("unchecked")
	public void finup (String name, ArenaMan manager) {
		super.finup(name, manager);
		actualDurations = new ActualArenaDurations(this);
		solo = teamSlots == 1;
		maxPlayers = teams.size() * teamSlots;
		forEachTeam(team -> team.finup(this));
	}
	
	public Team getTeam (String name) {
		return teams.get(name);
	}
	
	public void forEachTeam (Consumer<Team> action) {
		for (Team team : teams.values()) action.accept(team);
	}
	
	/* ======================================================================================= */
	
	public void broadcast        (String message)                   { for (Team team : teams.values()) team.broadcast        (message);        }
	public void broadcastError   (String message)                   { for (Team team : teams.values()) team.broadcastError   (message);        }
	public void broadcastWarning (String message)                   { for (Team team : teams.values()) team.broadcastWarning (message);        }
	public void broadcastSuccess (String message)                   { for (Team team : teams.values()) team.broadcastSuccess (message);        }
	public void broadcastColor   (TextFormat color, String message) { for (Team team : teams.values()) team.broadcastColor   (color, message); }
	public void broadcastColor   (char color,       String message) { for (Team team : teams.values()) team.broadcastColor   (color, message); }
	
	/* ======================================================================================= */
	
	public void onTick          (int second) {}
		   void onTick_internal (int second) {
		onTick(second);
	}
	
	public void onWaitStart          (int second) {}
		   void onWaitStart_internal (int second) {
		onWaitStart(second);
	}
	
	public void onWaitCancelled          () {}
		   void onWaitCancelled_internal () {
		onWaitCancelled();
	}
	
	public void onWaitTick          (int second) {}
		   void onWaitTick_internal (int second) {
		onWaitTick(second);
	}
	
	public void onWaitEnd          (int second) {}
		   void onWaitEnd_internal (int second) {
		onWaitEnd(second);
	}
	
	public void onWaitEndTick          (int second) {}
		   void onWaitEndTick_internal (int second) {
		onWaitEndTick(second);
	}
	
	public void onPreGame          (int second) {}
		   void onPreGame_internal (int second) {
		onPreGame(second);
		forEachTeam(team -> team.onPreGame_internal(second));
	}
	
	public void onPreGameTick          (int second) {}
		   void onPreGameTick_internal (int second) {
		onPreGameTick(second);
	}
	
	public void onGameStart          (int second) {}
		   void onGameStart_internal (int second) {
		onGameStart(second);
		forEachTeam(team -> team.onGameStart_internal(second));
	}
	
	public void onGameTick          (int second) {}
		   void onGameTick_internal (int second) {
		onGameTick(second);
	}
	
	public void onGameEnd          (int second) {}
		   void onGameEnd_internal (int second) {
		// todo event
		if (winData == null) {
			winData = forceWinDataOnEnd();
		}
		forEachTeam(team -> team.onGameEnd_internal(second));
	}
	
	public void onGameEndTick          (int second) {}
		   void onGameEndTick_internal (int second) {
		onGameEndTick(second);
	}
	
	/* ======================================================================================= */
	
	 /** @return False для отмены */
	public boolean onPreGamerJoin    (G gamer)            { return true; }
	public void    onGamerJoined     (G gamer, Team team) {}
	@SuppressWarnings("unchecked")
		   void onGamerJoin_internal (G gamer) {
		if (!onPreGamerJoin(gamer)) {
			return;
		}
		Team team = getTeamForJoin(gamer);
		if (team != null) {
			// joining
			onGamerJoined(gamer, team);
			gamer.onArenaJoined_internal(team);
		}
	}
	
	@Nullable
	public Team getTeamForJoin (G gamer) {
		return null; // todo
	}
	
	public boolean canJoinInTeam (Team team) {
		return false; // todo
	}
	
	public void onPreGamerLeave       (G gamer) {}
	public void onGamerLeaved         (G gamer) {}
		   void onGamerLeave_internal (G gamer) {
		onPreGamerLeave(gamer);
		// leaving
		onGamerLeaved(gamer); // todo redo to team
		gamer.onArenaLeaved_internal();
	}
	
	/* ======================================================================================= */
	
	public void onPreTeleportAfterEnd        () {}
	public void onAfterTeleportAfterEnd      () {}
	/** @noinspection Convert2MethodRef*/
	@SuppressWarnings("unchecked")
		   void onGameEndStateEnded_internal () {
		this.onPreTeleportAfterEnd();
		// teleport all
		this.onAfterTeleportAfterEnd();
		forEachTeam(team -> team.afterTeleportAfterGame_internal());
		this.reload();
	}
	
	public void onReloadStart () {}
	public void onReloaded    () {}
	
	/* ======================================================================================= */
	
	@SuppressWarnings("unchecked")
	public WinData newWinDataObj (Team winnerTeam, G bestGamer) {
		return Util.newInstance((Class<WinData>) Util.superGenericClass(this, 4), this, winnerTeam, bestGamer);
	}
	
	/** Этот метод должен вернуть новый WinData, используя newWinDataObj
	 *  в случае, если игра должна быть окончена (переведена в статус GAME_END) */
	@Nullable
	public abstract WinData onCheckWin ();
	
	/** Этот метод должен вернуть новый WinData, используя newWinDataObj
	 *  когда время игры вышло и необходимо принудительно определить победителя */
	@NotNull
	public abstract WinData forceWinDataOnEnd ();
	
	public void checkWin () {
		if (this.state == ArenaState.GAME && this.winData == null) {
			WinData winData = onCheckWin();
			if (winData != null) {
				this.winData = winData;
				this.ticker.switchType();
			}
		}
	}
	
	/* ======================================================================================= */
	
	protected void runTask () {
		TaskApi.repeat(1, ticker = new LordArenaTicker(this));
	}
	
	protected void cancelTask () {
		ticker.cancel();
		ticker = null;
	}
	
	public boolean isReadyStartTicker () {
		return false; // todo
	}
	
	public boolean isTicking () {
		return ticker != null;
	}
	
	public void startTickerIfReady () {
		if (isTicking()) return;
		if (isReadyStartTicker()) {
			runTask();
		}
	}
	
	public void stopTickerIfNotReady () {
		if (isTicking()) {
			if (isReadyStartTicker()) return;
			cancelTask();
			onWaitCancelled();
		}
	}
	
	/* ======================================================================================= */
	
	/** @return True, если мир арены загружен */
	public boolean isWorldLoaded () {
		return world != null;
	}
	
	/** Загружает мир арены, если он не загружен
	 * @return True, если мир загружен */
	public boolean loadWorld () {
		if (isWorldLoaded()) return true;
		boolean loaded = server.loadLevel(worldName);
		if (loaded) {
			world = server.getLevelByName(worldName);
			logger.info("Loaded world [" + worldName + "] for arena [" + name + "]");
		}
		else logger.error("Unable to load world " + worldName + "] for arena [" + name + "]");
		return loaded;
	}
	
	/** Выгружает мир арены, если он загружен */
	public void unloadWorld () {
		if (isWorldLoaded()) {
			getManager().getCore().getServer().unloadLevel(world);
			logger.info("Unloaded world [" + worldName + "] from arena [" + name + "]");
		}
	}
	
	/* ======================================================================================= */
	
	public AdvFolder worldFolder () {
		return getManager().getWorldsFolder().getChild(worldName);
	}
	
	public AdvFolder copyFolder () {
		return getManager().getCopiesFolder().getChild(worldName);
	}
	
	public boolean restoreBackupWorld () {
		unloadWorld();
		
		var copyF = copyFolder();
		if (!copyF.exists()) {
			logger.error("Copy folder does not exists! Unable to reload arena");
			return false;
		}
		var worldF = worldFolder();
		
		// any checks
		
		worldF.fullDelete();
		copyF.copyContentIn(worldF);
		
		return true;
	}
	
	public void reload () { // todo redo async
		setState(ArenaState.RELOAD);
		long start = System.currentTimeMillis();
		logger.info("Starts reloading...");
		
		onReloadStart();
		winData = null;
		gamersCount = 0;
		
		if (((MiniGameConfig)getManager().getConfig()).isBackupWorlds()) {
			if (!restoreBackupWorld()) {
				return;
			}
		}
		
		unloadWorld(); // контрольная выгрузка
		
		setState(ArenaState.WAIT);
		logger.info("Reloaded! It took " + (System.currentTimeMillis() - start));
		onReloaded();
	}
	
	/* ======================================================================================= */
}
