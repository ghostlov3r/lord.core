package lord.core.util;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.scheduler.Task;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lord.core.Forms;
import lord.core.api.TaskApi;

import java.util.HashMap;
import java.util.Map;

public class DebugInfo {
	
	private static Map<Player, PopupTask> tasks;
	
	private static int num = 1024 * 1024;
	
	public synchronized static void enablePopup (Player player) {
		if (tasks == null) {
			tasks = new HashMap<>();
		}
		PopupTask task = new PopupTask(player);
		tasks.put(player, task);
		TaskApi.repeat(1, task);
	}
	
	public static void disablePopups () {
		if (tasks != null) {
			for (Task task : tasks.values()) {
				task.cancel();
			}
			tasks.clear();
			tasks = null;
		}
	}
	
	public static void disablePopup (Player player) {
		if (tasks != null) {
			Task task = tasks.get(player);
			if (task != null) {
				task.cancel();
				tasks.remove(player);
			}
			if (tasks.isEmpty()) {
				tasks = null;
			}
		}
	}
	
	@RequiredArgsConstructor
	static class PopupTask extends Task {
		private final Player player;
		@Override
		public void onRun (int i) {
			if (!this.player.isOnline()) {
				this.cancel();
				disablePopup(this.player);
				return;
			}
			this.player.sendPopup(generateString(this.player));
		}
	}
	
	private static String generateString (Player player)
	{
		val builder = new StringBuilder();
		val server = player.getServer();
		val runtime = Runtime.getRuntime();
		
		/* double totalMB = NukkitMath.round(((double) runtime.totalMemory()) / num, 2);
		double usedMB = NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / num, 2);
		double maxMB = NukkitMath.round(((double) runtime.maxMemory()) / num, 2); */
		
		Location loc = player.getLocation();
		int x = loc.getFloorX();
		int z = loc.getFloorZ();
		
		double yaw =  NukkitMath.round(player.getYaw(), 2);
		double pitch =  NukkitMath.round(player.getPitch(), 2);
		
		builder.append("TРS: ").append(server.getTicksPerSecond())
			   .append(" | Load: ").append(server.getTickUsage())
			   .append("% | Х: ").append(Util.toChunk(x))
			   .append(" | Z: ").append(Util.toChunk(z)).append(Forms.NEXT_LINE)
			   .append("Used: ").append(NukkitMath.round((double) (runtime.totalMemory() - runtime.freeMemory()) / num, 2))
			   .append("mb / ").append(NukkitMath.round(((double) runtime.totalMemory()) / num, 2))
			   .append("mb / ").append(NukkitMath.round(((double) runtime.maxMemory()) / num, 2))
			   .append("mb || XYZ: ").append(x).append(", ")
			   .append(loc.getFloorY()).append(", ").append(z).append(", ")
			   .append(yaw).append(", ").append(pitch);
		
		/*return "TРS: " + tps + " | Load: " + load + "% | RegionStore: " + regionCount + " | Х: " + cX + " | Z: " + cZ +
				"\nUsеd: " + usedMB + "mb / " + totalMB + "mb / " + maxMB + "mb || XYZ: "
				+ x + ", " + y + ", " + z + ", " + yaw + ", " + pitch;*/
		
		return builder.toString();
	}
	
}