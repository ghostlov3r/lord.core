package lord.core.minigame.mggamer;

import cn.nukkit.network.SourceInterface;
import lombok.Getter;
import lord.core.gamer.Gamer;
import lord.core.minigame.arena.ArenaState;
import lord.core.minigame.arena.LordArena;
import lord.core.minigame.arena.LordArenaTeam;
import lord.core.util.Util;
import org.jetbrains.annotations.Nullable;

/**
 * Этот тип игрока предназначен для использования в минииграх.
 *
 * @param <GData> Тип данных игрока
 * @param <GMan> Тип менеджера игроков
 * @param <Team> Тип игровой команды игрока
 * @param <TempStats> Тип данных игрока во время игры
 */
@Getter
public abstract class MGGamer<GData extends MGGamerData, GMan extends MGGamerMan, Arena extends LordArena, Team extends LordArenaTeam, TempStats extends GamerTempStats>
	extends Gamer<GData, GMan> {
	
	/* ======================================================================================= */
	
	/** Команда, в которой находится игрок */
	@Nullable private Team team;
	
	/** Временные данные игрока во время игры на арене */
	@Nullable private TempStats tempStats;
	
	public MGGamer (SourceInterface interfaz, Long clientID, String ip, int port) {
		super(interfaz, clientID, ip, port);
	}
	
	/* ======================================================================================= */
	
	/** @return True, если игрок в команде.
	 * Это также значит, что игрок на арене
	 * и если идет игра, то он еще не проиграл */
	public boolean inTeam () {
		return team != null;
	}
	
	public boolean hasTempStats () {
		return tempStats != null;
	}
	
	@Nullable @SuppressWarnings("unchecked")
	public Arena getArena () {
		return team == null ? null : (Arena) team.getArena();
	}
	
	@Nullable
	public ArenaState getArenaState () {
		return team == null ? null : team.getArena().getState();
	}
	
	/** @return True, если игрок выбыл из игры.
	 * False, если игрок играет либо нет данных об игре */
	public boolean isDroppedOut () {
		return tempStats != null && tempStats.isDroppedOut();
	}
	
	public void setDroppedOut () {
		if (tempStats != null) tempStats.setDroppedOut(true);
	}
	
	/* ======================================================================================= */
	// todo конфиг звуков
	/* ======================================================================================= */
	
	/** @return True, если тиму можно сменить */
	public boolean onPreTeamChange (Team newTeam) {
		// check
		return true;
	}
	public void onTeamChanged         (Team newTeam) {}
		   void onTeamChange_internal (Team newTeam) {
		if (onPreTeamChange(newTeam)) {
			// changing
			onTeamChanged(newTeam);
		}
	}
	
	/* ======================================================================================= */
	
	@SuppressWarnings("unchecked")
	public TempStats newTempStatsObj () {
		return Util.newInstance((Class<TempStats>) Util.superGenericClass(this, 5), this);
	}
	
	public TempStats onTempStatsCreate () {
		return newTempStatsObj();
	}
	
	public void onArenaJoined (Team team) {}
	public void onArenaJoined_internal (Team team) {
		onArenaJoined(team);
	}
	
	public void onArenaLeaved () {}
	public void onArenaLeaved_internal () {
		onArenaLeaved();
	}
	
	public void onStatePreGame (int second) {}
	public void onStatePreGame_internal (int second) {
		onStatePreGame(second);
	}
	
	public void onGameStart (int second) {}
	public void onGameStart_internal (int second) {
		this.tempStats = onTempStatsCreate();
		onGameStart(second);
	}
	
	public void onGameEnd (int second) {}
	public void onGameEnd_internal (int second) {
		onGameEnd(second);
	}
	
	public void afterTeleportAfterEnd () {}
	public void afterTeleportAfterEnd_internal () {
		afterTeleportAfterEnd();
		this.tempStats = null;
	}
	
}
