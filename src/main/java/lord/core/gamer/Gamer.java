package lord.core.gamer;

import dev.ghostlov3r.beengine.Server;
import dev.ghostlov3r.beengine.block.Position;
import dev.ghostlov3r.beengine.entity.util.Location;
import dev.ghostlov3r.beengine.form.Form;
import dev.ghostlov3r.beengine.form.SimpleForm;
import dev.ghostlov3r.beengine.item.Item;
import dev.ghostlov3r.beengine.network.handler.InGamePacketHandler;
import dev.ghostlov3r.beengine.player.GameMode;
import dev.ghostlov3r.beengine.player.Player;
import dev.ghostlov3r.beengine.player.PlayerInfo;
import dev.ghostlov3r.beengine.scheduler.AsyncTask;
import dev.ghostlov3r.beengine.scheduler.Scheduler;
import dev.ghostlov3r.beengine.utils.TextFormat;
import dev.ghostlov3r.beengine.world.World;
import dev.ghostlov3r.math.Vector3;
import dev.ghostlov3r.minecraft.MinecraftSession;
import dev.ghostlov3r.minecraft.protocol.v113.packet.WorldSoundEvent;
import dev.ghostlov3r.nbt.NbtMap;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lord.core.Lord;
import lord.core.game.group.Group;
import lord.core.game.rank.Rank;
import lord.core.union.UnionDataProvider;

import java.net.InetAddress;
import java.util.*;

@Accessors(fluent = true)
@Getter
public class Gamer extends Player {

	public Runnable onShift = null;

	/* ======================================================================================= */
	public long joinTime;
	private String chatFormat;

	public long lastChatTime = 0;
	public String lastMessage = "";
	public int badMessages = 0;
	
	private Group group;
	private Rank rank;

	/* ======================================================================================= */

	public static final long NOT_MUTED = -1;
	public static final long MUTED_FOREVER = 0;

	@Setter protected int money;
	protected int rankExp;
	@Setter protected int playedMinutes; // todo

	@Setter protected String email;
	@Setter protected String vklink;

	public Set<String> friends = new HashSet<>();

	public long lastAuthMillis;
	public String lastAuthIP;
	public boolean authChecked;
	public byte[] password;
	public boolean handlingPassword;

	/* ======================================================================================= */

	public Gamer (MinecraftSession session, PlayerInfo info, boolean authenticated, NbtMap data) {
		super(session, info, authenticated, data);
	}

	private NbtMap realData;
	private Location realSpawn;

	@Override
	public void init (Location spawn) {
		if (initialized) {
			throw new IllegalStateException("Player '" + name() + "' is already initialized");
		}
		NbtMap data = Lord.unionHandler.provider().readData(name());
		if (data == null) {
			logger().error("Has not union data for "+name());
			disconnect("Union data is absent");
			return;
		}
		readUnionData(data);
		if (!authChecked) {
			realSpawn = spawn.toLocation();
			Position authPos = Lord.auth.world.getSpawnPosition();
			spawn.setWorld(authPos.world());
			spawn.setXYZFrom(authPos);
		}
		super.init(spawn);
	}

	@Override
	protected void initEntity() {
		super.initEntity();
		setRank(rankIdx);
		setGroup(groupName);
	}

	@Override
	public final void readSaveData(NbtMap nbt) {
		if (authChecked) {
			readSaveData0(nbt);
		} else {
			realData = nbt;
			readSaveData0(NbtMap.EMPTY);
		}
	}

	protected void readSaveData0 (NbtMap nbt) {
		super.readSaveData(nbt);
	}

	public void writeOriginalPlayerSaveData (NbtMap.Builder nbt) {
		super.writeSaveData(nbt);
	}

	/* ======================================================================================= */

	public void writeUnionData (NbtMap.Builder nbt) {
		nbt.setString("group", group.key());
		nbt.setInt("rank", rank.key());
		nbt.setInt("rankExp", rankExp);
		nbt.setInt("money", money);
		nbt.setInt("playedMinutes", playedMinutes);
		if (email != null) {
			nbt.setString("email", email);
		}
		if (vklink != null) {
			nbt.setString("vklink", vklink);
		}
		if (password != null) {
			nbt.setByteArray("password", password);
		}
		if (lastAuthIP != null) {
			nbt.setLong("lastAuthMillis", lastAuthMillis);
			nbt.setString("lastAuthIP", lastAuthIP);
		}
	}

	private int rankIdx;
	private String groupName;

	public void readUnionData(NbtMap nbt) {
		rankExp = nbt.getInt("rankExp", 0);
		money = nbt.getInt("money", 0);
		playedMinutes = nbt.getInt("playedMinutes", 0);

		email = nbt.getString("email", null);
		vklink = nbt.getString("vklink", null);

		rankIdx = (nbt.getInt("rank", Lord.ranks.defaultRank().key()));
		groupName = (nbt.getString("group", Lord.groups.defaultGroup().key()));
		password = nbt.getByteArray("password", null);
		lastAuthIP = nbt.getString("lastAuthIP", null);
		lastAuthMillis = nbt.getLong("lastAuthMillis", 0);

		authChecked = isAuthorizedAutomatically();
	}

	public void saveUnionData () {
		UnionDataProvider saver = Lord.unionHandler.provider();
		if (saver != null) {
			NbtMap.Builder builder = NbtMap.builder();
			writeUnionData(builder);
			saver.writeData(name(), builder.build());
		}
	}

	public boolean isRegistered () {
		return password != null;
	}

	public boolean isAuthorized () {
		return authChecked && world != Lord.auth.world;
	}

	/** Действия после успешного ввода пароля */
	public void setAuthorized() {
		if (authChecked) {
			return;
		}
		authChecked = true;
		lastAuthMillis = System.currentTimeMillis();
		lastAuthIP = session().address().getAddress().getHostName();
		saveUnionData();

		if (!realData.isEmpty()) {
			readSaveData(realData);
			inventoryManager().syncAll(false);
		}
		teleport(realSpawn, this::onSuccessAuth);
		realData = null;
		realSpawn = null;
	}

	@Override
	public boolean shouldSpawnTo(Player player) {
		return isAuthorized() && super.shouldSpawnTo(player);
	}

	/** Если при входе 2-часовая сессия не истекла, выполняется сразу
	 * иначе только после авторизации / регистрации */
	public void onSuccessAuth () {

		if (!group().isDefault()) {
			Lord.broadcast("§l+ " + group().getPrefix() + "§f " + name());
		}

		joinTitle();
	}

	@SneakyThrows
	private boolean isAuthorizedAutomatically () {
		if (lastAuthIP == null) {
			return false;
		}
		if (lastAuthMillis + Lord.instance.config().keepAuthorizedUnit.toMillis(Lord.instance.config().keepAuthorized) > System.currentTimeMillis()) {
			return session().address().getAddress().equals(InetAddress.getByName(lastAuthIP));
		}
		return false;
	}

	/* ======================================================================================= */

	/** @return Имя в нижнем регистре */
	public String lowerName() {
		return name().toLowerCase();
	}
	
	/** @return True, если указан имейл */
	public boolean hasMail () {
		return this.email != null;
	}
	
	/** @return True, если указан имейл */
	public boolean hasVk () {
		return this.vklink != null;
	}
	
	/* ======================================================================================= */
	
	public void prefixMessage        (String message)                   { sendMessage(Lord.instance.config().getPrefix() + message);                             }
	public void prefixErrorMessage   (String message)                   { sendMessage(Lord.instance.config().getPrefix() + TextFormat.RED + message);            }
	public void prefixWarningMessage (String message)                   { sendMessage(Lord.instance.config().getPrefix() + TextFormat.GOLD + message);           }
	public void prefixSuccessMessage (String message)                   { sendMessage(Lord.instance.config().getPrefix() + TextFormat.GREEN + message);          }
	public void prefixColorMessage   (TextFormat color, String message) { sendMessage(Lord.instance.config().getPrefix() + color + message);                     }
	public void prefixColorMessage   (char color,       String message) { sendMessage(Lord.instance.config().getPrefix() + TextFormat.ESCAPE + color + message); }
	
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
		setGroup(Lord.groups.get(groupName));
	}
	
	/** Дает группу игроку, обновляя права и неймтэг */
	public void setGroup (Group group) {
		if (group == null)
			group = Lord.groups.defaultGroup();
		this.group = group;
		updateNameTag();
	}
	
	public void setRank (int rankName) {
		setRank(Lord.ranks.get(rankName));
	}
	
	public void setRank (Rank rank) {
		if (rank == null)
			rank = Lord.ranks.defaultRank();
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

		this.setRank(newRank);
		addRankExp(newExp - maxExp); // Добавление остатка при достижении нового ранга
	}
	
	/* ======================================================================================= */
	
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
		if (Lord.teleport.requested(this)) {
			return;
		}
		Lord.teleport.request(this, loc, seconds);
	}
	
	public void joinTitle () {
		this.sendTitle("NEKRAFT", "Survival Elite Server", 20, 40, 15);
	}
	
	/* ======================================================================================= */

	
	/* ======================================================================================= */
	
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
			this.sendMessage(Lord.instance.config().getPrefix() + "Инвентарь сохранен!");
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
			this.sendMessage(Lord.instance.config().getPrefix() + "Потеряно " + remove + " Koins");
		}
		
		Scheduler.delay(120, () -> {
			if (this.isOnline()) {
				this.teleport(World.defaultWorld().getSpawnPosition());
				this.setGamemode(GameMode.SURVIVAL);
				this.setImmobile(false);
			}
		});
	}

	public void removeMoney(int money) {
		this.money -= money;
	}

	private static final String[] BAD_WORDS = {
		".ru", ".com", "191"
	};

	/** TRUE Если сообщение спам */
	public boolean isSpam (String message) {
		long current = System.currentTimeMillis();
		if ((current - lastChatTime) < 2200) {
			return true;
		}
		if (lastMessage.equals(message)) {
			return true;
		}
		for (String badMessage : BAD_WORDS) {
			if (message.contains(badMessage)) { // todo мощный фильтр в декодинге пакета
				return true;
			}
		}
		lastChatTime = current;
		lastMessage = message;
		return false;
	}
}