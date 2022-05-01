package lord.core.game.warp;

import beengine.entity.util.Location;
import beengine.player.Player;
import beengine.util.DiskMap;
import beengine.util.TextFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lord.core.Lord;
import lord.core.gamer.Gamer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
public class WarpMan extends DiskMap<String, Warp> {
	
	@Setter @Accessors(fluent = true)
	private BiConsumer<Player, Warp> onWarpCreate;
	
	@Setter @Accessors(fluent = true)
	private Function<Gamer, List<String>> playerWarpNames;

	private WarpConfig config;
	
	public WarpMan () {
		super(Lord.instance.dataPath().resolve("warps"), Warp.class);
		enableAutoLoad();
		config = WarpConfig.loadFromDir(path().resolve("cfg"), WarpConfig.class);
		config.getForceLoadNames().forEach(this::load);
	}
	
	/*@Override
	protected LordCommand cmdInternal () {
		return new WarpCommand();
	}*/
	
	public boolean teleportOrMess (Player player, String warpName) {
		Warp warp = get(warpName);
		if (warp == null) {
			player.sendMessage(TextFormat.RED + "Извините, точка телепортации не найдена");
			return false;
		}
		if (warp.teleport(player)) {
			return true;
		} else {
			player.sendMessage(TextFormat.RED + "Извините, эта точка сейчас не доступна");
			return false;
		}
	}
	
	/**
	 * Создает новый обьект варпа
	 * @param name Имя варпа
	 * @param player Владелец
	 * @param open Публичный ли варп
	 */
	public Warp create (String name, Player player, boolean open) {
		return create(name, player, player.name(), open);
	}
	
	/**
	 * Создает новый обьект варпа
	 * @param name Имя варпа
	 * @param loc Локация
	 * @param owner Имя владельца
	 * @param open Публичный ли варп
	 */
	public Warp create (String name, Location loc, String owner, boolean open) {
		Warp warp = new Warp(this, name);
		warp.location = loc.toLocation();
		warp.location.setWorld(null);
		warp.worldName = loc.world().uniqueName();
		warp.opened = open;
		warp.ownerName = owner;
		warp.whiteList = new ArrayList<>();
		return warp;
	}
	
}
