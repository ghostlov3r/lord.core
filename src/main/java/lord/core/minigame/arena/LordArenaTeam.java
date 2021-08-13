package lord.core.minigame.arena;

import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import lord.core.minigame.mggamer.MGGamer;
import lord.core.util.Util;
import lord.core.util.json.JsonSkip;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Элемент HashMap в LordArena
 * Представляет собой одну из команд игроков на этой арене
 *
 * @param <Arena> Тип арены
 * @param <G> Тип игроков
 * @author ghostlov3r
 */
@Getter
public abstract class LordArenaTeam<Arena extends LordArena, G extends MGGamer, TempStats extends TeamTempStats> {
	
	/** Арена, которой принадлежит эта команда */
	@JsonSkip private Arena arena;
	
	/** Игроки, которые сейчас находятся в этой команде,
	 * будь то во время игры или в лобби ожидания.
	 * Если арена сольная, то здесь всегда будет максимум один человек. */
	@JsonSkip private List<G> gamers;
	
	@JsonSkip private TextFormat color;
	
	@JsonSkip private TempStats tempStats;
	
	/* ===================================== JSON ============================================ */
	
	/** Имя этой команды */
	private String name;
	
	/** Отображаемое имя этой команды */
	private String displayName;
	
	/** Цвет TextFormat этой команды */
	private char colorChar;
	
	/** Спаун-позиции этой команды.
	 * Здесь должно находиться столько же элементов,
	 * сколько слотов в команде.
	 * После загрузки с диска мир будет null, и только
	 * перед телепортом он поимеет ссылку на мир. */
	private List<Location> spawnLocations;
	
	/* ======================================================================================= */
	
	public void finup (Arena arena) {
		this.arena = arena;
		gamers = new ArrayList<>(arena.getTeamSlots());
		color = TextFormat.getByChar(colorChar);
	}
	
	public G getSoloGamer () {
		return gamers.get(0);
	}
	
	public Location getSoloLocation () {
		return spawnLocations.get(0);
	}
	
	public boolean isSolo () {
		return arena.isSolo();
	}
	
	/* ======================================================================================= */
	
	public void forEachGamer (Consumer<G> action) {
		if (arena.isSolo()) {
			G gamer = gamers.get(0);
			if (gamer != null) action.accept(gamer);
		} else for (G gamer : gamers) action.accept(gamer);
	}
	
	public void broadcast (String message) {
		if (arena.isSolo()) {
			G gamer = gamers.get(0);
			if (gamer != null) gamer.prefixMessage(message);
		} else for (G gamer : gamers) gamer.prefixMessage(message);
	}
	
	public void broadcastError (String message) {
		if (arena.isSolo()) {
			G gamer = gamers.get(0);
			if (gamer != null) gamer.prefixErrorMessage(message);
		} else for (G gamer : gamers) gamer.prefixErrorMessage(message);
	}
	
	public void broadcastWarning (String message) {
		if (arena.isSolo()) {
			G gamer = gamers.get(0);
			if (gamer != null) gamer.prefixWarningMessage(message);
		} else for (G gamer : gamers) gamer.prefixWarningMessage(message);
	}
	
	public void broadcastSuccess (String message) {
		if (arena.isSolo()) {
			G gamer = gamers.get(0);
			if (gamer != null) gamer.prefixSuccessMessage(message);
		} else for (G gamer : gamers) gamer.prefixSuccessMessage(message);
	}
	
	public void broadcastColor (TextFormat color, String message) {
		if (arena.isSolo()) {
			G gamer = gamers.get(0);
			if (gamer != null) gamer.prefixColorMessage(color, message);
		} else for (G gamer : gamers) gamer.prefixColorMessage(color, message);
	}
	
	public void broadcastColor (char color, String message) {
		if (arena.isSolo()) {
			G gamer = gamers.get(0);
			if (gamer != null) gamer.prefixColorMessage(color, message);
		} else for (G gamer : gamers) gamer.prefixColorMessage(color, message);
	}
	
	/* ======================================================================================= */
	
	@SuppressWarnings("unchecked")
	public TempStats newTempStatsObj () {
		return Util.newInstance((Class<TempStats>) Util.superGenericClass(this, 3), this);
	}
	
	public TempStats onTempStatsCreate () {
		return newTempStatsObj();
	}
	
	public void onPreGame          (int second) {}
		   void onPreGame_internal (int second) {
		onPreGame(second);
		forEachGamer(gamer -> gamer.onStatePreGame_internal(second));
	}
	
	public void onGameStart          (int second) {}
		   void onGameStart_internal (int second) {
		this.tempStats = onTempStatsCreate();
		onGameStart(second);
		forEachGamer(gamer -> gamer.onGameStart_internal(second));
	}
	
	public void onGameEnd          (int second) {}
		   void onGameEnd_internal (int second) {
		onGameEnd(second);
		forEachGamer(gamer -> gamer.onGameEnd_internal(second));
	}
	
	public void afterTeleportAfterGame          () {}
		   /** @noinspection Convert2MethodRef*/
		   void afterTeleportAfterGame_internal () {
		afterTeleportAfterGame();
		forEachGamer(gamer -> gamer.afterTeleportAfterEnd());
		this.tempStats = null;
	}
	
}
