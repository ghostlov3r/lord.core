package lord.core.game.warp;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lord.core.LordCore;
import lord.core.command.service.LordCommand;
import lord.core.gamer.Gamer;
import lord.core.mgrbase.manager.LordManFCA;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
public class WarpMan extends LordManFCA<Warp, WarpConfig, LordCore> {
	
	@Setter @Accessors(fluent = true)
	private BiConsumer<Player, Warp> onWarpCreate;
	
	@Setter @Accessors(fluent = true)
	private Function<Gamer, List<String>> playerWarpNames;
	
	public WarpMan () {
		getConfig().getForceLoadNames().forEach(this::loadToMap);
	}
	
	@Override
	protected LordCommand cmdInternal () {
		return new WarpCommand();
	}
	
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
		return create(name, player.getLocation(), player.getName(), open);
	}
	
	/**
	 * Создает новый обьект варпа
	 * @param name Имя варпа
	 * @param loc Локация
	 * @param owner Имя владельца
	 * @param open Публичный ли варп
	 */
	public Warp create (String name, Location loc, String owner, boolean open) {
		Warp warp = new Warp();
		warp.x = loc.getX();
		warp.y = loc.getY();
		warp.z = loc.getZ();
		warp.yaw = loc.getYaw();
		warp.pitch = loc.getPitch();
		warp.worldName = loc.getLevel().getName();
		warp.opened = open;
		warp.ownerName = owner;
		warp.whiteList = new ArrayList<>();
		warp.finup(name, this);
		return warp;
	}
	
}
