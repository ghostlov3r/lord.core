package lord.core.minigame;

import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.beengine.block.Blocks;
import dev.ghostlov3r.beengine.block.blocks.BlockAir;
import dev.ghostlov3r.beengine.event.block.BlockBreakEvent;
import dev.ghostlov3r.beengine.event.block.BlockPlaceEvent;
import dev.ghostlov3r.beengine.form.Form;
import dev.ghostlov3r.beengine.form.SimpleForm;
import dev.ghostlov3r.beengine.inventory.PlayerInventory;
import dev.ghostlov3r.beengine.item.ItemFactory;
import dev.ghostlov3r.beengine.item.Items;
import dev.ghostlov3r.beengine.player.GameMode;
import dev.ghostlov3r.beengine.player.Player;
import dev.ghostlov3r.beengine.player.PlayerInfo;
import dev.ghostlov3r.beengine.utils.TextFormat;
import dev.ghostlov3r.beengine.world.Sound;
import dev.ghostlov3r.beengine.world.WorldManager;
import dev.ghostlov3r.minecraft.MinecraftSession;
import dev.ghostlov3r.nbt.NbtMap;
import lombok.Getter;
import lombok.experimental.Accessors;
import lord.core.gamer.Gamer;
import lord.core.minigame.arena.*;
import lord.core.minigame.data.GameMap;

import javax.annotation.Nullable;

/**
 * Этот тип игрока предназначен для использования в минииграх.
 */
@Accessors(fluent = true)
@Getter
public class MGGamer extends Gamer {

	/** Команда, в которой находится игрок */
	@Nullable private Team team;
	
	/** Временные данные игрока во время игры на арене */
	@Nullable private GamerGameContext gameCtx;

	public MiniGame manager;

	public boolean shouldHidePlayers = false;

	public GameMap vote = null;

	public MGGamer (MinecraftSession interfaz, PlayerInfo clientID, boolean ip, NbtMap port) {
		super(interfaz, clientID, ip, port);
	}

	/* ======================================================================================= */
	
	/** @return True, если игрок в команде. */
	public boolean inTeam () {
		return team != null;
	}

	public boolean inLobby () {
		return !inTeam();
	}

	public boolean inWaitLobby () {
		ArenaState s = arenaState();
		return s == ArenaState.WAIT || s == ArenaState.STAND_BY || s == ArenaState.WAIT_END;
	}

	public boolean inGame () {
		return arenaState() == ArenaState.GAME && !isDroppedOut();
	}
	
	public Arena arena() {
		return team == null ? null : team.arena();
	}
	
	@Nullable
	public ArenaState arenaState() {
		return team == null ? null : team.arena().state();
	}
	
	/** @return True, если игрок выбыл из игры.
	 * False, если игрок играет либо нет данных об игре */
	public boolean isDroppedOut () {
		return gameCtx.isDroppedOut();
	}
	
	public void dropOut() {
		gameCtx.setDroppedOut(true);

		setGamemode(GameMode.SPECTATOR);
		setHealth(maxHealth());
		extinguish();

		if (world.getBlock(addY(5)) instanceof BlockAir) {
			motion.y += 4;
		} else {
			motion.y += world.getHighestBlockAt(floorX(), floorZ()) - y + 1;
		}

		if (shouldSpawnDeadGamerOnDropOut()) {
			gameCtx.dead = new DeadGamer(this);
		}

		updateInventory();

		sendSubTitle(TextFormat.RED+"Вы выбыли из игры");

		arena().onGamerDropOut(this);
		onDropOut0();
	}

	protected boolean shouldSpawnDeadGamerOnDropOut () {
		return true;
	}

	protected void onDropOut0 () {
		// NOOP
	}

	public void showGameInfo () {
		sendForm(Form.simple());
	}

	public void showStatistics () {
		sendForm(Form.simple());
	}

	public void updateInventory () {
		PlayerInventory inv = inventory();

		switch (arenaState()) {
			case STAND_BY, WAIT -> {
				inv.clear();
				giveTeamChooseItem();
				giveArenaLeaveItem();

				inv.setItem(3, Items.COMPASS().setCustomName(decorateMenuItemName("Голосовать за карту"))
						.onInteract((p, b) -> {
							SimpleForm form = Form.simple();
							arena().type().maps().forEach(map -> {
								form.button(map.displayName, __ -> {
									vote = map;
									sendMessage(TextFormat.GREEN+"Вы проголосовали за "+map.displayName);
									score().set(3, "Голос за "+map.displayName);
								});
							});
							sendForm(form);
						})
				);
			}
			case WAIT_END -> {
				inv.setItem(3, ItemFactory.air());
				inv.setItem(9, ItemFactory.air());
			}
			case PRE_GAME -> {
				inv.setItem(0, ItemFactory.air());
			}
			case GAME -> {
				if (isDroppedOut()) {
					inv.clear();
					giveArenaLeaveItem();
					giveSpectatorItem();
				}
				else {
					giveGameItems();
				}
			}
			case GAME_END -> {
				inv.clear();
				giveArenaLeaveItem();
			}
			default -> {
				giveLobbyItems();
			}
		}
	}

	private void giveLobbyItems () {
		PlayerInventory inv = inventory();
		inv.clear();
		giveHidingItem();
		inv.setItem(1, Items.PLAYER_HEAD()
				.setCustomName(decorateMenuItemName("Друзья"))
				.onInteract((p, b) -> {
					showFriends();
				}));
		inv.setItem(5, Items.BOOK()
				.setCustomName(decorateMenuItemName("Информация о игре"))
				.onInteract((p, b) -> {
					showGameInfo();
				}));
		inv.setItem(8, Items.PAPER()
				.setCustomName(decorateMenuItemName("Статистика"))
				.onInteract((p, b) -> {
					showStatistics();
				}));
		inv.setItem(9, Items.ENCHANTED_GOLDEN_APPLE()
				.setCustomName(decorateMenuItemName("Донат"))
				.onInteract((p, b) -> {
					showDonateInfo();
				}));
	}

	private void giveSpectatorItem () {
		inventory.setItem(0, Items.COMPASS()
				.setCustomName(decorateMenuItemName("Телепортер"))
				.onInteract((p, b) -> {
					SimpleForm form = Form.simple();

					arena().forEachGamer(rawGamer -> {
						MGGamer gamer = (MGGamer) rawGamer;
						form.button(gamer.name(), player -> {
							if (gamer.arena() == arena() && !gamer.isDroppedOut()) {
								teleport(gamer);
							}
						});
					});

					sendForm(form);
				}));
	}

	private void giveArenaLeaveItem () {
		inventory.setItem(9, Blocks.ACACIA_DOOR().asItem()
				.setCustomName(decorateMenuItemName("Покинуть"))
				.onInteract((p, b) -> {
					leaveArena();
				}));
	}

	private void giveTeamChooseItem () {
		if (!arena().isSolo()) {
			inventory.setItem(0, Blocks.WOOL().setColor(team.color()).asItem()
					.onInteract((p, b) -> {
						SimpleForm form = Form.simple();

						arena().forEachTeam(team -> {
							form.button(team.textColor() + team.displayName(), player -> {
								if (team == this.team) {
									sendMessage("Вы уже в этой команде!");
								}
								else if (team.isJoinable()) {
									this.team.gamers().remove(this);
									this.team = team;
									this.team.gamers().add(this);
									sendMessage("Теперь вы в команде "+team.textColor() + team.displayName());
									giveTeamChooseItem();
								}
								else {
									sendMessage(TextFormat.RED+"В этой команде слишком много игроков!");
								}
							});
						});

						sendForm(form);
					}));
		}
	}

	private void giveHidingItem () {
		inventory().setItem(0, Items.CLOCK()
				.setCustomName(decorateMenuItemName(shouldHidePlayers ? "Показать игроков" : "Скрыть игроков"))
				.onInteract((p, b) -> {
					shouldHidePlayers = !shouldHidePlayers;
					if (shouldHidePlayers) {
						for (Player viewer : viewers()) {
							hidePlayer(viewer);
						}
					} else {
						showAllPlayers();
					}
					giveHidingItem();
				}));
	}

	public void giveGameItems () {
		// NOOP
	}

	public String decorateMenuItemName (String name) {
		return TextFormat.GREEN + manager.config().menuItemDecorSymbol + " " + TextFormat.GOLD + name + " " + TextFormat.GREEN + manager.config().menuItemDecorSymbol;
	}

	public void updateScoreboard () {
		switch (arenaState()) {
			case STAND_BY, WAIT -> {
				score().set(0, "");
				updateArenaPlayerCountScoreInfo();
				if (arenaState() == ArenaState.WAIT) {
					score().set(3, "");
				}
			}
			case PRE_GAME, GAME -> {
				score().set(1, "Карта: "+arena().map().key());
				updateArenaPlayerCountScoreInfo();
			}
			default -> {
				score().hide();
				score().show();
				score().set(0, "");
				updateOnlineScoreInfo();
				score().set(2, "");
			}
		}
	}

	public void updateOnlineScoreInfo() {
		if (arenaState() == null) {
			score().set(1, "В сети: " + Server.onlinePlayers().length);
		}
	}

	public void updateArenaPlayerCountScoreInfo() {
		if (arenaState() == ArenaState.GAME) {
			updateArenaTeamsStatesScoreInfo0();
		} else {
			updateArenaPlayerCountScoreInfo0();
		}
	}

	protected void updateArenaPlayerCountScoreInfo0 () {
		score().set(1, "Игроков: "+ arena().gamersCount() + "/"+arena().type().maxPlayers());
	}

	protected void updateArenaTeamsStatesScoreInfo0() {
		if (arena().isSolo()) {
			score().set(3, "Игроков: "+ arena().aliveGamersCount() + "/"+arena().type().maxPlayers());
		} else {
			int i = 3;
			for (Team arenaTeam : arena().teams()) {
				score().set(i++, arenaTeam.coloredName() + ": "+(arenaTeam.isDroppedOut() ? "Проиграли" : "Играют"));
			}
		}
	}

	public void updateCountdownScoreInfo () {
		switch (arenaState()) {
			case STAND_BY -> score().set(2, "Ждем остальных...");
			case WAIT -> score().set(2, "До начала "+arena().ticker().second()+" сек.");
			case WAIT_END -> score().set(2, "");
			case PRE_GAME -> score().set(2, "Игра через "+arena().ticker().second()+" cек.");
			case GAME -> score().set(2, "Конец игры через "+arena().ticker().second()+" cек.");
		}
	}

	/* ======================================================================================= */

	public void onLobbyJoin () {
		manager.waitLobby().playersUnsafe().values().forEach(player -> {
			MGGamer gamer = (MGGamer) player;

			if (shouldHidePlayers) {
				hidePlayer(gamer);
				if (gamer.shouldHidePlayers) {
					gamer.hidePlayer(this);
				}
			}
			else {
				showPlayer(gamer);
				if (!gamer.shouldHidePlayers) {
					gamer.showPlayer(this);
				}
			}
		});

		setGamemode(GameMode.ADVENTURE);
		updateScoreboard();
		// setAllowFlight(manager.config().isLobbyDoubleJump());
	}

	public void onLobbyQuit () {
		if (shouldHidePlayers) {
			showAllPlayers();
		}
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
	public GamerGameContext newTempStatsObj () {
		return new GamerGameContext(this);
	}
	
	public GamerGameContext onTempStatsCreate () {
		return newTempStatsObj();
	}

	public void doJoinIn (Team team) {
		this.team = team;
		team.gamers().add(this);
		updateInventory();
		broadcastSound(Sound.ENDERMAN_TELEPORT, asList());
		onLobbyQuit();
		onArenaJoined0(team);
	}

	public void leaveArena () {
		Arena arena = arena();
		arena.onPreGamerLeave(this);
		team.gamers().remove(this);
		team = null;
		updateInventory();
		teleport(WorldManager.get().defaultWorld().getSpawnPosition());
		broadcastSound(Sound.DOOR, asList());
		gameCtx = null;
		vote = null;
		arena.onGamerLeaved(this);
		onLobbyJoin();
		afterArenaLeave0();
	}

	protected void onArenaJoined0(Team team) {
		// NOOP
	}

	protected void afterArenaLeave0() {
		// NOOP
	}

	public final void onPreGame() {
		teleportToGameWorld();
		vote = null;
		updateScoreboard();
		onStatePreGame0();
	}

	public void teleportToGameWorld () {
		teleport(team.spawnLocationOf(this));
	}

	protected void onStatePreGame0() {
		// NOOP
	}

	public final void onGameStart() {
		this.gameCtx = onTempStatsCreate();
		updateInventory();
		onGameStart0();
	}

	protected void onGameStart0() {
		// NOOP
	}

	public final void onGameEnd() {
		updateInventory();
		onGameEnd0();
	}

	protected void onGameEnd0() {
		// NOOP
	}

	public void teleportToWaitLobby () {
		manager.waitLobby().playersUnsafe().values().forEach(player -> {
			MGGamer gamer = (MGGamer) player;
			if (gamer.arena() != arena()) {
				hidePlayer(gamer);
				gamer.hidePlayer(this);
			}
			else {
				showPlayer(gamer);
				gamer.showPlayer(this);
			}
		});
		teleport(manager.waitLobby().getSpawnPosition());
	}

	public final void afterGameEnd() {
		teleportToWaitLobby();
		setGamemode(GameMode.ADVENTURE);
		updateInventory();
		afterGameEnd0();
		this.gameCtx = null;
	}
	protected void afterGameEnd0() {}


	public void onBlockBreak (BlockBreakEvent<MGGamer> event) {

	}

	public void onBlockPlace (BlockPlaceEvent<MGGamer> event) {

	}
}
