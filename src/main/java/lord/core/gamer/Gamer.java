package lord.core.gamer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.ghostlov3r.beengine.entity.util.Location;
import dev.ghostlov3r.beengine.form.Form;
import dev.ghostlov3r.beengine.form.SimpleForm;
import dev.ghostlov3r.beengine.item.Item;
import dev.ghostlov3r.beengine.player.GameMode;
import dev.ghostlov3r.beengine.player.Player;
import dev.ghostlov3r.beengine.player.PlayerInfo;
import dev.ghostlov3r.beengine.scheduler.Scheduler;
import dev.ghostlov3r.beengine.utils.TextFormat;
import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.beengine.world.WorldManager;
import dev.ghostlov3r.math.Vector3;
import dev.ghostlov3r.minecraft.MinecraftSession;
import dev.ghostlov3r.minecraft.protocol.v113.packet.WorldSoundEvent;
import dev.ghostlov3r.nbt.NbtMap;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lord.core.LordCore;
import lord.core.game.auth.RegisterData;
import lord.core.game.group.Group;
import lord.core.game.rank.Rank;
import java.util.*;

@Accessors(fluent = true)
@Getter
public abstract class Gamer extends Player {
	
	/* ======================================================================================= */
	
	public static Gamer from (Player player) {
		return (Gamer) player;
	}
	
	/* ======================================================================================= */
	
	private final LordCore core;
	private final String   prefix;

	private boolean        authorized;
	public  long           joinTime;
	
	private String         chatFormat;
	
	public long           lastChatTime = 0;
	public String         lastMessage = "";
	public int            badMessages = 0;
	
	private Group          group;
	private Rank           rank;

	/* ======================================================================================= */

	public static final long NOT_MUTED = -1;
	public static final long MUTED_FOREVER = 0;


	/** Баланс           */  @Setter protected int    money;
	/** Опыт ранка       */          protected int    rankExp;
	/** Время игры мин   */  @Setter protected int    playedMinutes; // todo
	/** Время посл. auth */          protected long   lastAuth;
	/** Посл. IP auth    */          protected String lastAuthIP;
	/** Пароль           */          protected String password;

	/** Почта            */  @Setter protected String   email;
	/** Ссылка вк        */  @Setter protected String   vklink;

	public Set<String> friends;

	/* ======================================================================================= */

	@Override
	public void writeSaveData(NbtMap.Builder nbt) {
		super.writeSaveData(nbt);
		nbt.setString("group", group.key());
		nbt.setInt("rank", rank.key());
		nbt.setInt("rankExp", rankExp);
		nbt.setInt("money", money);
		nbt.setInt("playedMinutes", playedMinutes);
		if (lastAuthIP != null) {
			nbt.setString("lastAuthIP", lastAuthIP);
			nbt.setLong("lastAuth", lastAuth);
		}
		if (password != null) {
			nbt.setString("password", password);
		}
		if (email != null) {
			nbt.setString("email", email);
		}
		if (vklink != null) {
			nbt.setString("vklink", vklink);
		}
	}

	@Override
	public void readSaveData(NbtMap nbt) {
		super.readSaveData(nbt);

		setRank(nbt.getInt("rank"));
		setGroup(nbt.getString("group"));

		rankExp = nbt.getInt("rankExp");
		money = nbt.getInt("money");
		playedMinutes = nbt.getInt("playedMinutes");

		lastAuthIP = nbt.getString("lastAuthIP", null);
		lastAuth = nbt.getLong("lastAuth", 0);
		password = nbt.getString("password", null);
		email = nbt.getString("email", null);
		vklink = nbt.getString("vklink", null);
	}

	/* ======================================================================================= */

	@SuppressWarnings("unchecked")
	public Gamer (MinecraftSession interfaz, PlayerInfo clientID, boolean ip, NbtMap port) {
		super(interfaz, clientID, ip, port);
		this.core = LordCore.instance();
		this.prefix = core.config().getPrefix();
		
		// 2 часа
		authorized = lastAuthIP != null
				&& session().address().equals(this.lastAuthIP)
				&& (System.currentTimeMillis() - this.lastAuth) < 7_200_000L;
	}
	
	/* ======================================================================================= */

	/** @return Имя в нижнем регистре */
	public String lowerName() {
		return name().toLowerCase();
	}
	
	/** @return True, если зарегистрирован */
	public boolean isRegistered() {
		return this.password != null;
	}
	
	/** @return True, если указан имейл */
	public boolean hasMail () {
		return this.email != null;
	}
	
	/** @return True, если указан имейл */
	public boolean hasVk () {
		return this.vklink != null;
	}

	public void setRegData (RegisterData data) {
		this.password = data.password;
	}
	
	/* ======================================================================================= */
	
	public void prefixMessage        (String message)                   { sendMessage(prefix + message);                             }
	public void prefixErrorMessage   (String message)                   { sendMessage(prefix + TextFormat.RED + message);            }
	public void prefixWarningMessage (String message)                   { sendMessage(prefix + TextFormat.GOLD + message);           }
	public void prefixSuccessMessage (String message)                   { sendMessage(prefix + TextFormat.GREEN + message);          }
	public void prefixColorMessage   (TextFormat color, String message) { sendMessage(prefix + color + message);                     }
	public void prefixColorMessage   (char color,       String message) { sendMessage(prefix + TextFormat.ESCAPE + color + message); }
	
	/* ======================================================================================= */

	public void showFriends () {
		SimpleForm form = Form.simple();
		friends.forEach(form::button);
		sendForm(form);
	}

	public void showDonateInfo () {
		SimpleForm form = Form.simple();
		form.button("Лох - 50р");
		form.button("Гангстер - 200р");
		form.button("Школьник 80 лвл - 1000р");
		form.button("Трахнул мать админа - 5000р");
		sendForm(form);
	}

	/** Дает группу игроку, обновляя права и неймтэг */
	public void setGroup (String groupName) {
		setGroup(core.groupMan().get(groupName));
	}
	
	/** Дает группу игроку, обновляя права и неймтэг */
	public void setGroup (Group group) {
		if (group == null)
			group = core.groupMan().getDefaultGroup();
		this.group = group;
		updateNameTag();
	}
	
	public void setRank (int rankName) {
		setRank(core.rankMan().get(rankName));
	}
	
	public void setRank (Rank rank) {
		if (rank == null)
			rank = core.rankMan().getDefaultRank();
		this.rank = rank;
	}
	
	/**
	 * Добавляет опыт ранга и апает ранг, если достаточноо опыта.
	 * @param exp Количество опыта
	 */
	public void addRankExp (int exp) {
		int maxExp = rank.maxExp();
		int newExp = rankExp + exp;
		
		if (newExp <= maxExp) {
			rankExp = newExp;
			return;
		}
		if (!rank.hasNext()) {
			if (rankExp < maxExp) rankExp = maxExp;
			return;
		}
		Rank newRank = rank.getNext();

		addRankExp(newExp - maxExp); // Добавление остатка при достижении нового ранга
		
		this.setRank(rank);
	}
	
	/* ======================================================================================= */
	
	/** Действия после успешного ввода пароля */
	public void setAuthorized() {
		core.auth().allowActions(this);
		lastAuth = System.currentTimeMillis();
		lastAuthIP = session().address().getAddress().getHostAddress();
		
		authorized = true;
		
		onSuccessAuth();
	}
	
	/** Если при входе 2-часовая сессия не истекла, выполняется сразу
	 * иначе только после авторизации / регистрации */
	public void onSuccessAuth () {
		
		if (!group.isDefault()) {
			LordCore.broadcast("§l+ " + group().getPrefix() + "§f " + name());
		}
		
		joinTitle();
	}
	
	/** Обновляет неймтег и шаблон для чата, нужно при авторизации или смене статуса */
	public void updateNameTag () {
		if (group().isDefault()) {
			this.setNameTag("§o§8" + name());
			this.chatFormat = "§7" + name() + " §8-> §f";
		} else {
			String prefix = this.group().getPrefix();
			this.setNameTag("§l+ " + prefix + "§o§8" + name());
			this.chatFormat = "§7(" + prefix + "§7) §f" + name() + " §8-> §f";
		}
	}
	
	/** Телепортиция с предварительным отсчетом времени через Title */
	public void delayedTeleport (int seconds, int x, int y, int z) {
		this.delayedTeleport(seconds, x, y, z, this.world());
	}
	
	public void delayedTeleport (int seconds, int x, int y, int z, World level) {
		this.delayedTeleport(seconds, new Location(x, y, z, level, this.yaw, this.pitch));
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

	
	/* ======================================================================================= */
	
	/** Кик через 2 секунды */
	public void delayedKick (String reason) {
		Scheduler.delay(40, () -> this.disconnect("", reason));
	}
	
	public void playSound (WorldSoundEvent.SoundId sound) {
		var pk = new WorldSoundEvent();
		pk.sound = sound;
		// todo pk.xxx
		session().sendPacket(pk);
	}
	
	public void simulateDeath () {
		if (this.isOnFire()) {
			this.extinguish();
		}
		this.setHealth(20);
		this.setGamemode(GameMode.SPECTATOR);
		
		Vector3 pos = this.toVector();
		if (group.key().equals("diamond")) {
			this.sendMessage(core.config().getPrefix() + "Инвентарь сохранен!");
		} else {
			for (Item item : this.inventory().contents()) {
				this.world().dropItem(pos, item);
			}
			this.inventory().clear();
		}
		
		this.teleport(new Location(pos.x, pos.y + 6, pos.z, this.world(), 0, 0));
		this.setImmobile(true);
		this.sendTitle("§cПроизошла смерть", "Вы будете возвращены на точку возрождения", 10, 30, 30);
		
		int remove = this.money / 20;
		if (remove > 0) {
			this.removeMoney(remove);
			this.sendMessage(core.config().getPrefix() + "Потеряно " + remove + " Koins");
		}
		
		Scheduler.delay(120, () -> {
			if (this.isOnline()) {
				this.teleport(WorldManager.get().defaultWorld().getSpawnPosition());
				this.setGamemode(GameMode.SURVIVAL);
				this.setImmobile(false);
			}
		});
	}

	public void removeMoney(int money) {
		this.money -= money;
	}
}