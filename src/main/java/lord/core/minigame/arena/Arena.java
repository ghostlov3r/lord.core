package lord.core.minigame.arena;

import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.beengine.block.blocks.BlockSign;
import dev.ghostlov3r.beengine.block.utils.SignText;
import dev.ghostlov3r.beengine.event.entity.EntityDamageByEntityEvent;
import dev.ghostlov3r.beengine.event.entity.EntityDamageEvent;
import dev.ghostlov3r.beengine.scheduler.Scheduler;
import dev.ghostlov3r.beengine.utils.TextFormat;
import dev.ghostlov3r.beengine.world.Sound;
import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.common.Utils;
import dev.ghostlov3r.log.Logger;
import dev.ghostlov3r.math.FRand;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lord.core.minigame.MGGamer;
import lord.core.minigame.MiniGame;
import lord.core.minigame.data.ArenaType;
import lord.core.minigame.data.GameMap;

import java.util.*;
import java.util.function.Consumer;

/**
 * Элемент менеджера LordArenaMan
 * Представляет собой арену какой-либо миниигры
 *
 * @author ghostlov3r
 */
@Accessors(fluent = true)
@Getter
public class Arena
{
	private int id;
	private ArenaState state = ArenaState.STAND_BY;
	private ArenaType type;
	private Ticker ticker = null;
	private GameMap map = null;
	private World gameWorld = null;

	private List<Team> teams = new ArrayList<>();

	private int gamersCount;

	private WinData winData; // Не Null после победы или конца таймера

	protected Logger logger;
	protected MiniGame manager;

	protected List<BlockSign> stateSigns = new ArrayList<>();

	@SneakyThrows
	public Arena (MiniGame manager, ArenaType type, int id) {
		this.type = type;
		this.id = id;
		this.manager = manager;
		this.logger = Server.logger().withPrefix("Arena #"+id);

		for (int i = 0; i < type.teamCount(); i++) {
			teams.add(manager.teamType().getConstructor(Arena.class, int.class).newInstance(this, i));
		}
	}

	/* ======================================================================================= */
	
	public void forEachTeam (Consumer<Team> action) {
		teams.forEach(action);
	}

	public void forEachGamer (Consumer<MGGamer> action) {
		forEachTeam(team -> {
			team.gamers().forEach(action);
		});
	}

	public boolean isEmpty () {
		return gamersCount == 0;
	}

	public boolean isFull () {
		return gamersCount == type().maxPlayers();
	}

	public boolean isSolo () {
		return type.teamSlots() == 1;
	}

	public int aliveGamersCount () {
		int c = 0;
		for (Team team : teams) {
			c += team.aliveGamersCount();
		}
		return c;
	}

	public int aliveTeamsCount () {
		int c = 0;
		for (Team team : teams) {
			if (!team.isDroppedOut()) {
				++c;
			}
		}
		return c;
	}

	/* ======================================================================================= */

	public void setState (ArenaState newState) {
		ArenaState oldState = this.state;
		this.state = newState;

		switch (newState) {
			case STAND_BY -> {
				if (oldState == ArenaState.WAIT) {
					onWaitCancelled();
				}
				else {
					this.afterGameEnd0();
					forEachTeam(Team::afterGameEnd);
					map.worldFactory().closeWorld(gameWorld);
				}
			}
			case WAIT -> {
				onWaitStart();
			}
			case WAIT_END -> {
				chooseMap();
				map.worldFactory().createWorld().onResolve(promise -> {
					gameWorld = promise.result();
				});
				onWaitEnd();
			}
			case PRE_GAME -> {
				onPreGame();
				forEachTeam(Team::onPreGame);
			}
			case GAME -> {
				forEachGamer(gamer -> {
					gamer.broadcastSound(Sound.NOTE(Sound.NoteInstrument.PIANO, 50), gamer.asList());
					gamer.sendSubTitle(TextFormat.GREEN + "Игра началась!");
				});
				onGameStart();
				forEachTeam(Team::onGameStart);
			}
			case GAME_END -> {
				if (winData == null) {
					winData = forceWinDataOnEnd();
				}
				onGameEnd();
				forEachTeam(Team::onGameEnd);
			}
		}
		if (isTicking()) {
			ticker.refreshSecond();
		}
		updateSignState();
	}

	protected void chooseMap () {
		var votes = new Reference2IntOpenHashMap<GameMap>();
		forEachGamer(gamer -> {
			if (gamer.vote != null) {
				votes.addTo(gamer.vote, 1);
			}
		});
		GameMap best = null;
		if (!votes.isEmpty()) {
			int max = 0;
			for (Reference2IntMap.Entry<GameMap> entry : votes.reference2IntEntrySet()) {
				if (entry.getIntValue() > max) {
					max = entry.getIntValue();
					best = entry.getKey();
				}
			}
		}
		if (best == null) {
			best = type.maps().get(FRand.random().nextInt(type.maps().size()));
		}
		this.map = best;
	}

	/* ======================================================================================= */

	public void onWaitCancelled() {
		int max = type.durationOfState(ArenaState.WAIT);
		forEachGamer(gamer -> {
			gamer.xpManager().setXpAndProgress(max, 1f);
			gamer.updateScoreboard();
		});
		onWaitCancelled0();
	}

	protected void onWaitStart() {
		// NOOP
	}

	protected void onWaitCancelled0 () {
		// NOOP
	}

	protected void onWaitEnd() {
		// NOOP
	}

	protected void onPreGame() {
		// NOOP
	}

	protected void onGameStart() {
		// NOOP
	}

	protected void onGameEnd() {
		// NOOP
	}

	protected void afterGameEnd0() {}

	/* ======================================================================================= */

	final void onTick (int second) {
		switch (state) {
			case WAIT -> {
				int max = type.durationOfState(ArenaState.WAIT);
				float newProgress = (1f / max) * second;
				forEachGamer(gamer -> {
					gamer.xpManager().setXpAndProgressNoEvent(second, newProgress);
				});
				onWaitTick(second);
			}
			case WAIT_END -> {
				forEachGamer(gamer -> {
					gamer.broadcastSound(Sound.NOTE(Sound.NoteInstrument.PIANO, 50), gamer.asList());
					gamer.sendTitle(String.valueOf(second), "", 5, 20, 5);
				});
				onWaitEndTick(second);
			}
			case PRE_GAME -> onPreGameTick(second);
			case GAME -> onGameTick(second);
			case GAME_END -> onGameEndTick(second);
		}

		forEachGamer(MGGamer::updateCountdownScoreInfo);
	}

	protected void onTick0 (int second) {
		// NOOP
	}

	protected void onWaitTick(int second) {
		// NOOP
	}

	protected void onWaitEndTick(int second) {
		// NOOP
	}

	protected void onPreGameTick(int second) {
		// NOOP
	}

	protected void onGameTick(int second) {
		// NOOP
	}

	protected void onGameEndTick(int second) {
		// NOOP
	}

	/* ======================================================================================= */

	public final void onDamage (EntityDamageEvent event) {
		if (!isSolo()) {
			if (event.entity() instanceof MGGamer gamer) {
				if (event instanceof EntityDamageByEntityEvent edbee) {
					if (edbee.damager() instanceof MGGamer damager) {
						if (damager.team() == gamer.team()) {
							event.cancel();
							return;
						}
					}
				}
			}
		}
		onDamage0(event);
	}

	protected void onDamage0 (EntityDamageEvent event) {
		// NOOP
	}

	public final void onGamerDropOut (MGGamer gamer) {
		if (gamer.team().isDroppedOut()) {
			onTeamDropOut(gamer.team());
		}
		forEachGamer(MGGamer::updateArenaPlayerCountScoreInfo);
		onGamerDropOut0(gamer);
	}

	protected void onGamerDropOut0 (MGGamer gamer) {
		// NOOP
	}

	final void onTeamDropOut (Team team) {
		if (aliveTeamsCount() == 1) {
			onLastAliveTeam(team);
		}
	}

	protected void onTeamDropOut0 (Team team) {
		// NOOP
	}

	final void onLastAliveTeam (Team team) {
		onLastAliveTeam0(team);
	}

	protected void onLastAliveTeam0 (Team team) {
		setState(ArenaState.GAME_END);
	}
	
	/* ======================================================================================= */

	public final boolean isJoinable () {
		return !type.maps().isEmpty() && isJoinable0();
	}

	protected boolean isJoinable0 () {
		return state == ArenaState.STAND_BY || state == ArenaState.WAIT;
	}

	public final void tryJoin (MGGamer gamer) {
		if (type.maps().isEmpty()) {
			gamer.sendTitle(TextFormat.RED+"Арена выключена", TextFormat.GOLD+"Войдите на другую арену");
			return;
		}
		if (!isJoinable()) {
			gamer.sendTitle(TextFormat.RED+"Арена сейчас не доступна", TextFormat.GOLD+"Ожидайте или войдите на другую арену");
			return;
		}

		Team team = getTeamForJoin(gamer);
		gamer.doJoinIn(team);
		gamer.teleportToWaitLobby();

		++gamersCount;

		startTickerIfReady();
		updateSignState();
		gamer.updateScoreboard();
		onGamerJoined0(gamer, team);
		broadcast(gamer.name() + " присоединился к "+ team.textColor()+team.displayName());

		if (isFull()) {
			setState(ArenaState.WAIT_END);
		}
	}

	protected void onGamerJoined0(MGGamer gamer, Team team) {
		// NOOP
	}

	public Team getTeamForJoin (MGGamer gamer) {
		int min = type().teamSlots();
		Team best = null;

		for (Team team : teams) {
			if (team.isEmpty()) {
				return team;
			}
			else {
				if (team.gamers().size() < min) {
					min = team.gamers().size();
					best = team;
				}
			}
		}

		return best;
	}
	
	public final void onPreGamerLeave (MGGamer gamer) {
		onPreGamerLeave0(gamer);
	}

	public final void onGamerLeaved (MGGamer gamer) {
		broadcast(gamer.name() + " покинул игру");
		--gamersCount;
		stopTickerIfNotReady();
		updateSignState();
		forEachGamer(MGGamer::updateArenaPlayerCountScoreInfo);
		onGamerLeaved0(gamer);
	}

	protected void onPreGamerLeave0 (MGGamer gamer) {
		// NOOP
	}

	protected void onGamerLeaved0 (MGGamer gamer) {
		// NOOP
	}

	/* ======================================================================================= */

	public void endGameWith (WinData winData) {
		if (this.state == ArenaState.GAME && this.winData == null) {
			this.winData = winData;
			setState(ArenaState.GAME_END);
		}
	}
	
	/** Этот метод должен вернуть новый WinData, используя newWinDataObj
	 *  когда время игры вышло и необходимо принудительно определить победителя */
	public WinData forceWinDataOnEnd () {
		return new WinData(this, teams.get(FRand.random().nextInt(teams.size())));
	}
	
	/* ======================================================================================= */
	
	protected void runTask () {
		Scheduler.repeat(20, ticker = new Ticker(this));
	}
	
	protected void cancelTask () {
		ticker.cancel();
		ticker = null;
	}
	
	public boolean isReadyStartTicker () {
		return isSolo() ? gamersCount >= type.minPlayers() : teams.stream().allMatch(team -> team.gamers().size() >= type.minPlayers());
	}
	
	public boolean isTicking () {
		return ticker != null;
	}
	
	public void startTickerIfReady () {
		if (isTicking()) return;

		if (state != ArenaState.STAND_BY) {
			throw new IllegalStateException();
		}

		if (isReadyStartTicker()) {
			setState(ArenaState.WAIT);
			runTask();
		}
	}
	
	public void stopTickerIfNotReady () {
		if (isTicking() && state == ArenaState.WAIT) {
			if (!isReadyStartTicker()) {
				cancelTask();
				setState(ArenaState.STAND_BY);
			}
		}
	}

	public void updateSignState () {
		stateSigns.forEach(sign -> {
			sign.setText(new SignText(
					"Арена №"+id,
					type.maxPlayers() + "x" + type.teamCount(),
					"₽"+gamersCount +"/"+ type.maxPlayers()+"₽",
					type.maps().isEmpty() ? TextFormat.RED+"Выключена" : state.text()
				));
			sign.world().setBlock(sign, sign);
		});
	}

	/* ======================================================================================= */

	public void broadcast        (String message)                   { for (Team team : teams) team.broadcast        (message);        }
	public void broadcastError   (String message)                   { for (Team team : teams) team.broadcastError   (message);        }
	public void broadcastWarning (String message)                   { for (Team team : teams) team.broadcastWarning (message);        }
	public void broadcastSuccess (String message)                   { for (Team team : teams) team.broadcastSuccess (message);        }
	public void broadcastColor   (TextFormat color, String message) { for (Team team : teams) team.broadcastColor   (color, message); }
	public void broadcastColor   (char color,       String message) { for (Team team : teams) team.broadcastColor   (color, message); }

	/* ======================================================================================= */
}
