package lord.core.minigame.arena;

import dev.ghostlov3r.beengine.block.utils.DyeColor;
import dev.ghostlov3r.beengine.entity.util.Location;
import dev.ghostlov3r.beengine.utils.TextFormat;
import dev.ghostlov3r.common.Utils;
import lombok.Getter;
import lombok.experimental.Accessors;
import lord.core.minigame.Colors;
import lord.core.minigame.MGGamer;
import lord.core.minigame.data.GameMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Элемент HashMap в LordArena
 * Представляет собой одну из команд игроков на этой арене
 *
 * @author ghostlov3r
 */
@Accessors(fluent = true)
@Getter
public class Team {
	
	/** Арена, которой принадлежит эта команда */
	private Arena arena;
	
	/** Игроки, которые сейчас находятся в этой команде,
	 * будь то во время игры или в лобби ожидания.
	 * Если арена сольная, то здесь всегда будет максимум один человек. */
	private List<MGGamer> gamers;

	private TeamStats tempStats;

	/** Отображаемое имя этой команды */
	private String displayName;
	
	/** Цвет этой команды */
	private DyeColor color;

	private int id;
	
	/* ======================================================================================= */
	
	public Team (Arena arena, int id) {
		this.arena = arena;
		this.id = id;
		this.gamers = new ArrayList<>(arena.type().teamSlots());
		this.color = isSolo() ? DyeColor.WHITE : Colors.COLORS.get(id);
		this.displayName = color.name();
	}
	
	public MGGamer soloGamer() {
		return gamers.get(0);
	}

	public Location spawnLocationOf (MGGamer gamer) {
		for (int i = 0; i < gamers.size(); i++) {
			if (gamers.get(i) == gamer) {
				return arena.map().teams.get(i).locations().get(i).asLocation(arena.gameWorld());
			}
		}
		throw new RuntimeException();
	}
	
	public boolean isSolo () {
		return arena.isSolo();
	}

	public boolean isFull () {
		return gamers.size() == arena().type().teamSlots();
	}

	public boolean isEmpty () {
		return gamers.isEmpty();
	}

	public boolean isJoinable () {
		return !isFull();
	}

	public TextFormat textColor () {
		return Colors.asFormat(color);
	}

	public int aliveGamersCount () {
		int c = 0;
		for (MGGamer gamer : gamers) {
			if (!gamer.isDroppedOut()) {
				++c;
			}
		}
		return c;
	}

	public boolean isDroppedOut () {
		return aliveGamersCount() == 0;
	}

	public String coloredName () {
		return textColor() + displayName + TextFormat.RESET;
	}
	
	/* ======================================================================================= */
	
	public void broadcast (String message) {
		gamers.forEach(gamer -> gamer.prefixMessage(message));
	}
	
	public void broadcastError (String message) {
		gamers.forEach(gamer -> gamer.prefixErrorMessage(message));
	}
	
	public void broadcastWarning (String message) {
		gamers.forEach(gamer -> gamer.prefixWarningMessage(message));
	}
	
	public void broadcastSuccess (String message) {
		gamers.forEach(gamer -> gamer.prefixSuccessMessage(message));
	}
	
	public void broadcastColor (TextFormat color, String message) {
		gamers.forEach(gamer -> gamer.prefixColorMessage(color, message));
	}
	
	public void broadcastColor (char color, String message) {
		gamers.forEach(gamer -> gamer.prefixColorMessage(color, message));
	}
	
	/* ======================================================================================= */
	
	public TeamStats newTempStatsObj () {
		return new TeamStats(this);
	}
	
	public TeamStats onTempStatsCreate () {
		return newTempStatsObj();
	}

	final void onPreGame() {
		onPreGame0();
		gamers.forEach(MGGamer::onPreGame);
	}

	protected void onPreGame0() {
		// NOOP
	}

	final void onGameStart() {
		this.tempStats = onTempStatsCreate();
		onGameStart0();
		gamers.forEach(MGGamer::onGameStart);
	}

	protected void onGameStart0() {
		// NOOP
	}

	final void onGameEnd() {
		onGameEnd0();
		gamers.forEach(MGGamer::onGameEnd);
	}

	protected void onGameEnd0() {
		// NOOP
	}

	final void afterGameEnd() {
		afterGameEnd0();
		gamers.forEach(MGGamer::afterGameEnd);
		this.tempStats = null;
	}

	protected void afterGameEnd0() {
		// NOOP
	}
}
