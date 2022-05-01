package lord.core.gamer;

import beengine.block.Position;
import beengine.entity.util.Location;
import beengine.form.Form;
import beengine.form.SimpleForm;
import beengine.item.Item;
import beengine.minecraft.MinecraftSession;
import beengine.minecraft.data.skin.SkinData;
import beengine.minecraft.protocol.v113.packet.WorldSoundEvent;
import beengine.nbt.NbtMap;
import beengine.nbt.NbtType;
import beengine.player.GameMode;
import beengine.player.Player;
import beengine.player.PlayerInfo;
import beengine.scheduler.Scheduler;
import beengine.util.TextFormat;
import beengine.util.math.Vector3;
import beengine.world.Sound;
import beengine.world.World;
import fastutil.set.ShortSet;
import fastutil.set.impl.ShortHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lord.core.Lord;
import lord.core.game.group.Group;
import lord.core.game.rank.Rank;
import lord.core.union.UnionDataProvider;
import lord.core.util.LordNpc;

import java.net.InetAddress;
import java.util.*;
import java.util.function.BiFunction;

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
	private ShortSet gotRankRewards;

	/* ======================================================================================= */

	public static final long NOT_MUTED = -1;
	public static final long MUTED_FOREVER = 0;

	protected int goldMoney;
	protected int silverMoney;
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

	/*public static class Friend {
		String name;
		long lastPlayed;
	}*/

	public String tempRegPassNoForm;

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

	int realDistanceRequest = Integer.MIN_VALUE;

	@Override
	public void setViewDistance(int distance) {
		if (!isAuthorized()) {
			realDistanceRequest = distance;
			distance = 3;
		}
		super.setViewDistance(distance);
	}

	@Override
	protected void initEntity() {
		super.initEntity();
		if (gotRankRewards == null) {
			gotRankRewards = new ShortHashSet();
		}
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
		nbt.setInt("goldMoney", goldMoney);
		nbt.setInt("silverMoney", silverMoney);
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
		if (!gotRankRewards.isEmpty()) {
			nbt.setList("gotRankRewards", NbtType.SHORT, gotRankRewards);
		}
	}

	private int rankIdx;
	private String groupName;

	public void readUnionData(NbtMap nbt) {
		rankExp = nbt.getInt("rankExp", 0);
		goldMoney = nbt.getInt("goldMoney", 0);
		silverMoney = nbt.getInt("silverMoney", 0);
		playedMinutes = nbt.getInt("playedMinutes", 0);

		email = nbt.getString("email", null);
		vklink = nbt.getString("vklink", null);

		rankIdx = (nbt.getInt("rank", Lord.ranks.defaultRank().key()));
		groupName = (nbt.getString("group", Lord.groups.defaultGroup().key()));
		password = nbt.getByteArray("password", null);
		lastAuthIP = nbt.getString("lastAuthIP", null);
		lastAuthMillis = nbt.getLong("lastAuthMillis", 0);

		gotRankRewards = new ShortHashSet(nbt.getList("gotRankRewards", NbtType.SHORT, List.of()));

		authChecked = isAuthorizedAutomatically();
	}

	public void saveUnionData () {
		UnionDataProvider saver = Lord.unionHandler.provider();
		if (saver != null) {
			NbtMap.Builder builder = NbtMap.builder();
			writeUnionData(builder);
			logger().debug("Calling saver.writeData from saveUnionData");
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
		lastAuthIP = session().address().getAddress().getHostAddress();
		saveUnionData();

		if (!realData.isEmpty()) {
			readSaveData(realData);
			inventoryManager().syncAll(false);
		}
		teleport(realSpawn, () -> {
			if (realDistanceRequest != Integer.MIN_VALUE) {
				setViewDistance(realDistanceRequest);
				realDistanceRequest = Integer.MIN_VALUE;
			}
			onSuccessAuth();
		});
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
		form.title("Мои друзья");
		form.content("§cЭтот раздел в разработке!§r\n\n" +
				"Совсем скоро можно будет добавить друга и посмотреть, на каком сервере он играет, и даже пригласить к себе!");
		// friends.forEach(form::button);
		sendForm(form);
	}

	public void showDonateInfo () {
		SimpleForm form = Form.simple();
		form.title("Донат-бонусы");
		form.content("§cЭтот раздел в разработке!§r\n\n" +
				"Скоро здесь будет что-то интересное!");
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
		if (exp < 1) {
			return;
		}

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

		setRank(newRank);
		rankExp = 0;
		addRankExp(newExp - maxExp); // Добавление остатка при достижении нового ранга
	}
	
	/* ======================================================================================= */
	
	/** Обновляет неймтег и шаблон для чата, нужно при авторизации или смене статуса */
	public void updateNameTag () {
		if (group().isDefault()) {
			this.setNameTag("₽§l§7" + name()+"₽");
			this.chatFormat = "§7 $NAME §8-> §f$MESSAGE₽";
		} else {
			String prefix = this.group().getPrefix();
			this.setNameTag("₽§l" + prefix + " §f" + name()+"₽");
			this.chatFormat = "§7(" + prefix + "§7) §f$NAME §8-> §f$MESSAGE₽";
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
		this.sendTitle("§bL§eO§dR§aD", "§7Multi Mode Elite Server", 20, 40, 15);
		this.broadcastSound(Sound.NOTE(Sound.NoteInstrument.PIANO, 70), asList());
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
		
		int remove = this.goldMoney / 20;
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
		this.goldMoney -= money;
	}

	public LordNpc createNpc() {
		return createNpc(LordNpc::new);
	}

	public <T extends LordNpc> T createNpc (BiFunction<Location, SkinData, T> factory) {
		T npc = factory.apply(this, this.skin());
		NbtMap.Builder data = NbtMap.builder();
		writeOriginalPlayerSaveData(data);
		npc.readSaveData(data.build());
		return npc;
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

	public void showGiftMenu () {
		SimpleForm form = Form.simple();
		form.title("Награды");
		form.button("Награда за повышение уровня", __ -> showRankRewards());
		sendForm(form);
	}

	public void showRankRewards () {
		SimpleForm form = Form.simple();
		form.title("Награды за уровень");
		Rank rank = Lord.ranks.defaultRank();
		while (rank != null) {
			Rank r = rank;
			form.button("Уровень "+rank.key()+ (
				rank.key() > this.rank.key()
					? TextFormat.RED+" (Не доступно)"
					: (
						gotRankRewards.contains(rank.key().shortValue())
							? TextFormat.GRAY + " (Получено)"
							: TextFormat.AQUA + " ЗАБРАТЬ!"
					)
			), __ -> doGetRewardFor(r));
			rank = rank.getNext();
		}
		sendForm(form);
	}

	public void doGetRewardFor (Rank rank) {
		if (rank.key() > this.rank.key()) {
			sendMessage(TextFormat.RED+"Повысь свой уровень до "+rank.key()+"!");
		}
		else if (!gotRankRewards.add(rank.key().shortValue())) {
			sendMessage(TextFormat.GOLD+"Награда за "+rank.key()+" уровень уже получена");
		}
		else {
			actuallyGetRewardFor(rank);
		}
	}

	protected void actuallyGetRewardFor (Rank rank) {
		if (rank.moneyReward() > 0) {
			addSilverMoney(rank.moneyReward());
			sendMessage("Получено "+rank.moneyReward()+" серебра");
		}
		if (rank.goldReward() > 0) {
			addGoldMoney(rank.goldReward());
			sendMessage("Получено "+rank.goldReward()+" золота");
		}
	}

	public void setSilverMoney(int silverMoney) {
		this.silverMoney = silverMoney;
	}

	public void setGoldMoney(int goldMoney) {
		this.goldMoney = goldMoney;
	}

	public void addSilverMoney (int amount) {
		setSilverMoney(silverMoney() + amount);
	}

	public void addGoldMoney (int amount) {
		setGoldMoney(goldMoney() + amount);
	}

	public void removeSilverMoney (int amount) {
		addSilverMoney(-amount);
	}

	public void removeGoldMoney (int amount) {
		addGoldMoney(-amount);
	}

	public void incrementPlayedMinutes () {
		++playedMinutes;
	}
}