package lord.core.gamer;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.utils.TextFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Getter;
import lombok.var;
import lord.core.LordCore;
import lord.core.api.CoreApi;
import lord.core.api.TaskApi;
import lord.core.form.Form;
import lord.core.game.group.Group;
import lord.core.game.rank.Rank;
import lord.core.scoreboard.Scoreboard;
import lord.core.util.logger.ILordLogger;
import lord.core.util.logger.LordLogger;

import java.util.*;

@Getter
public abstract class Gamer<GD extends GamerData, GDM extends GamerMan> extends Player {
	
	/* ======================================================================================= */
	
	private static ILordLogger logger;
	
	private static void onEnable (LordCore core) {
		logger = LordLogger.get(Gamer.class);
	}
	
	public static Gamer from (Player player) {
		return (Gamer) player;
	}
	
	/* ======================================================================================= */
	
	/** Core                   */  private final LordCore core;
	/** Core                   */  private final String   prefix;
	/** Данные игрока          */  private final GD       data;
	
	/** Прошел ли авторизацию  */  private boolean        authorized;
	/** Время входа            */  public  long           joinTime;
	
	/** Шаблон чат-сообщения   */  private String         chatFormat;
	
	/** Время посл. сообщения  */  private long           lastChatTime = 0;
	/** Последнее сообщение    */  private String         lastMessage = "";
	/** Предупреждения о спаме */  private int            badMessages = 0;
	
	/**                        */  private Scoreboard     score;
	/**                        */  private Group          group;
	/**                        */  private Rank           rank;
	
	/**                        */  private Map<Integer, Form> formz = new HashMap<>(); // move to other plugin
	
	@SuppressWarnings("unchecked")
	public Gamer (SourceInterface interfaz, Long clientID, String ip, int port) {
		super(interfaz, clientID, ip, port);
		this.core = CoreApi.getCore();
		this.prefix = core.config().getPrefix();
		
		GDM manager = (GDM) this.core.gamerMan();
		GD data = (GD) manager.justLoad(this.getName());
		if (data == null) {
			this.data = (GD) manager.newDataFor(this); // todo Threads
		} else {
			data.gamer = this;
			this.data = data;
		}
		
		// 2 часа
		authorized = getAddress().equals(this.data.lastAuthIP) &&
			(System.currentTimeMillis() - this.data.lastAuth) < 7_200_000L;
	}
	
	/* ======================================================================================= */
	
	/** @return Имя в нижнем регистре */
	public String lowerName() {
		return username.toLowerCase();
	}
	
	/** @return True, если зарегистрирован */
	public boolean isRegistered() {
		return this.data.password != null;
	}
	
	/** @return True, если указан имейл */
	public boolean hasMail () {
		return this.data.email != null;
	}
	
	/** @return True, если указан имейл */
	public boolean hasVk () {
		return this.data.vklink != null;
	}
	
	/** Сохраняет на диск GamerData*/
	public void saveData () {
		if (this.isRegistered()) this.data.save();
	}
	
	/** Изменяет значения прав игроку */
	public void addAttachments (List<String> perms, boolean add) {
		if (perms != null) perms.forEach(perm -> this.addAttachment(core, perm, add));
	}
	
	/** Дает права игроку */
	public void addNukkitPermissions (List<String> perms) {
		this.addAttachments(perms, true);
	}
	
	/** Отбирает права у игрока */
	public void removeNukkitPermissions (List<String> perms) {
		this.addAttachments(perms, false);
	}
	
	/* ======================================================================================= */
	
	public void prefixMessage        (String message)                   { sendMessage(prefix + message);                             }
	public void prefixErrorMessage   (String message)                   { sendMessage(prefix + TextFormat.RED + message);            }
	public void prefixWarningMessage (String message)                   { sendMessage(prefix + TextFormat.GOLD + message);           }
	public void prefixSuccessMessage (String message)                   { sendMessage(prefix + TextFormat.GREEN + message);          }
	public void prefixColorMessage   (TextFormat color, String message) { sendMessage(prefix + color + message);                     }
	public void prefixColorMessage   (char color,       String message) { sendMessage(prefix + TextFormat.ESCAPE + color + message); }
	
	/* ======================================================================================= */
	
	/** Дает группу игроку, обновляя права и неймтэг */
	public void setGroup (String groupName) {
		setGroup(core.groupMan().get(groupName));
	}
	
	/** Дает группу игроку, обновляя права и неймтэг */
	public void setGroup (Group group) {
		if (group == null) {
			logger.warning("Attempted set group null to gamer " + this.getName());
			
			if (this.group == null) {
				setGroup(core.groupMan().getDefaultGroup());
				logger.info("Set default group to gamer " + this.getName());
			}
			return;
		}
		
		if (this.group != null) {
			if (this.group == group) return;
			removeNukkitPermissions(this.group.getPermissions());
		}
		
		this.group = group;
		data.groupName = group.getName();
		updateNameTag();
		
		addNukkitPermissions(this.group.getPermissions());
	}
	
	public void setRank (String rankName) {
		setRank(core.rankMan().get(rankName));
	}
	
	public void setRank (Rank rank) {
		if (rank == null) {
			logger.error("Attempted set rank null to gamer " + this.getName());
			
			if (this.rank == null) {
				setRank(this.core.rankMan().getDefaultRank());
				logger.info("Set default rank to gamer " + this.getName());
			}
			return;
		}
		
		if (this.rank != null) {
			if (this.rank == rank) {
				return;
			}
			if (this.rank.getPermissions() != null) {
				this.removeNukkitPermissions(Arrays.asList(this.rank.getPermissions()));
			}
		}
		
		this.data.rank = rank.getName();
		this.rank = rank;
		
		if (this.rank.getPermissions() != null) {
			this.addNukkitPermissions(Arrays.asList(this.rank.getPermissions()));
		}
	}
	
	/**
	 * Добавляет опыт ранга и апает ранг, если достаточноо опыта.
	 * @param exp Количество опыта
	 */
	public void addRankExp (int exp) {
		int maxExp = rank.getMaxExp();
		int newExp = data.rankExp + exp;
		
		if (newExp <= maxExp) {
			data.rankExp = newExp;
			return;
		}
		if (!rank.hasNext()) {
			if (data.rankExp < maxExp) data.rankExp = maxExp;
			return;
		}
		Rank newRank = rank.getNext();
		
		if (newRank == null) logger.error("getNext in Rank returned null");
		else addRankExp(newExp - maxExp); // Добавление остатка при достижении нового ранга
		
		this.setRank(rank);
	}
	
	/* ======================================================================================= */
	
	/** Действия после успешного ввода пароля */
	public void setAuthorized() {
		core.auth().allowActions(this);
		data.lastAuth = System.currentTimeMillis();
		data.lastAuthIP = this.getAddress();
		
		authorized = true;
		
		onSuccessAuth();
	}
	
	/** Если при входе 2-часовая сессия не истекла, выполняется сразу
	 * иначе только после авторизации / регистрации */
	@SuppressWarnings("unchecked")
	public void onSuccessAuth () {
		setRank(this.data.rank);
		setGroup(this.data.groupName);
		
		if (!group.isDefault()) {
			CoreApi.broadcast("§l+ " + getGroup().getPrefix() + "§f " + getName());
		}
		
		showScore();
		joinTitle();
		
		core.gamerMan().addAuthorizedGamer(this);
	}
	
	/** Обновляет неймтег и шаблон для чата, нужно при авторизации или смене статуса */
	public void updateNameTag () {
		if (getGroup().isDefault()) {
			this.setNameTag("§o§8" + this.getName());
			this.chatFormat = "§7" + this.getName() + " §8-> §f";
		} else {
			String prefix = this.getGroup().getPrefix();
			this.setNameTag("§l+ " + prefix + "§o§8" + this.getName());
			this.chatFormat = "§7(" + prefix + "§7) §f" + this.getName() + " §8-> §f";
		}
	}
	
	public void showScore () {
		if (!core.scoreMan().isEnabled()) return;
		if (score == null) score = new Scoreboard(this);
		score.show();
		core.scoreMan().onShow().accept(score);
		score.update();
	}
	
	public void hideScore () {
		if (this.score != null) this.score.hide();
	}
	
	/** Отправляет форму игроку */
	public void sendForm (Form form) {
		var packet = new ModalFormRequestPacket();
		try {
			packet.data = new JsonMapper().writeValueAsString(form);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return;
		}
		int id = 1;
		while (this.formz.containsKey(id)) id++;
		
		this.formz.put(packet.formId = id, form);
		this.dataPacket(packet);
	}
	
	/** Телепортиция с предварительным отсчетом времени через Title */
	public void delayedTeleport (int seconds, int x, int y, int z) {
		this.delayedTeleport(seconds, x, y, z, this.getLevel());
	}
	
	public void delayedTeleport (int seconds, int x, int y, int z, Level level) {
		this.delayedTeleport(seconds, new Location(x, y, z, this.getYaw(), this.getPitch(), level));
	}
	
	public void delayedTeleport (int seconds, Location loc) {
		if (core.teleport().requested(this)) {
			return;
		}
		core.teleport().request(this, loc, seconds);
	}
	
	public void joinTitle () {
		this.sendTitle("NEKRAFT", "Survival Elite Server", 20, 40, 15);
	}
	
	/* ======================================================================================= */
	
	/** Сообщение в чат от имени этого игрока */
	@Override
	public boolean chat (String message) {
		CoreApi.broadcast(this.chatFormat + message.trim());
		return true;
	}
	
	/** Отправляет сообщение этому игроку */
	@Override
	public void sendMessage(String message) {
		if (!this.isOnline() || !this.isAlive()) {
			return;
		}
		var pk = new TextPacket();
		pk.type = TextPacket.TYPE_RAW;
		pk.message = message;
		this.dataPacket(pk);
	}
	
	/** TRUE Если сообщение спам */
	private boolean isSpam (String message) {
		long current = System.currentTimeMillis();
		if ((current - this.lastChatTime) < 2000) {
			return true;
		}
		if (this.lastMessage.equals(message)) {
			return true;
		}
		this.lastChatTime = current;
		this.lastMessage = message;
		return false;
	}
	
	/** Обработка сообщения из TEXT_PACKET */
	public void wantSay (String message) {
		if (!this.authorized) {
			this.sendMessage(this.core.config().getPrefix() + "§cСперва выполните вход в аккаунт");
			return;
		}
		if (this.core.sanctions().isMuted(this)) {
			this.sendMessage("Вам запретили чат до " + new Date(this.data.getMutedUntil()).toGMTString());
			return;
		}
		if (this.isSpam(message)) {
			this.badMessages++;
			if (this.badMessages > 3) {
				this.badMessages = 0;
				this.core.sanctions().mute(this, "Спам-автомут", 0, 5);
				this.sendMessage("За спам Вам запрещен чат на 5 минут");
			} else {
				this.sendMessage("Спам запрещен");
			}
			return;
		}
		String[] lines = message.split("\n");
		if (lines[0].length() < 256) {
			this.chat(lines[0]);
		}
	}
	
	/* ======================================================================================= */
	
	/** Кик через 2 секунды */
	public void delayedKick (String reason) {
		TaskApi.delay(2, () -> this.close("", reason));
	}
	
	public void playSound (int sound) {
		var pk = new LevelSoundEventPacket();
		pk.sound = sound;
		// todo pk.xxx
		this.dataPacket(pk);
	}
	
	public void simulateDeath () {
		if (this.isOnFire()) {
			this.extinguish();
		}
		this.setHealth(20);
		this.setGamemode(Player.SPECTATOR);
		
		Vector3 pos = this.getPosition();
		if (this.data.groupName.equals("diamond")) {
			this.sendMessage(core.config().getPrefix() + "Инвентарь сохранен!");
		} else {
			for (Item item : this.getInventory().getContents().values()) {
				this.getLevel().dropItem(pos, item);
			}
			this.getInventory().clearAll();
		}
		
		this.teleport(new Location(pos.getX(), pos.getY() + 6, pos.getZ(), 0, 0, this.getLevel()));
		this.setImmobile(true);
		this.sendTitle("§cПроизошла смерть", "Вы будете возвращены на точку возрождения", 10, 30, 30);
		
		int remove = this.data.money / 20;
		if (remove > 0) {
			this.data.removeMoney(remove);
			this.sendMessage(core.config().getPrefix() + "Потеряно " + remove + " Koins");
		}
		
		TaskApi.delay(6, () -> {
			if (this.isOnline()) {
				this.teleport(LordCore.server.getDefaultLevel().getSpawnLocation());
				this.setGamemode(Player.SURVIVAL);
				this.setImmobile(false);
			}
		});
	}
	
}