package lord.core.game;

import cn.nukkit.Player;
import cn.nukkit.level.Location;
import cn.nukkit.scheduler.Task;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.var;
import lord.core.LordCore;
import lord.core.api.TaskApi;
import lord.core.mgrbase.manager.LordMan;
import lord.core.mgrbase.entry.LordEntry;
import lord.core.game.warp.Warp;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Teleport extends LordMan<Teleport.TeleportRequest, LordCore> {
	
	/** Имена телепортированных */ private List<String> completed = new ArrayList<>();
	
	public Teleport () {
		TaskApi.repeat(1, new TeleportTask(this));
		this.getLogger().enabled();
	}
	
	public void request (Player player, Warp warp, int delay) {
		request(player, warp.getLocation(), delay);
	}
	
	public void request (Player player, Warp warp, int delay, String message) {
		request(player, warp.getLocation(), delay, message);
	}
	
	public void request (Player player, Location location, int secondsDelay) {
		request(player, location, secondsDelay, null);
	}
	
	public void request (Player player, Location location, int secondsDelay, String message) {
		player.setTitleAnimationTimes(2, 20, 10);
		
		var request = new TeleportRequest(player, location, secondsDelay, message);
		request.finup(player.getName(), this);
		this.add(request);
	}
	
	public boolean requested (Player player) {
		return this.requested(player.getName());
	}
	
	public boolean requested (String name) {
		return this.exists(name);
	}
	
	private void addCompleted (Player player) {
		this.completed.add(player.getName());
	}
	
	public void cancel (Player player) {
		this.remove(player.getName());
	}
	
	@AllArgsConstructor
	protected static class TeleportRequest extends LordEntry<Teleport> {
		private final Player player;
		private final Location location;
		private int secondsDelay;
		@Nullable private String message;
		
		private void onTimer () {
			if (!player.isOnline()) {
				getManager().addCompleted(player);
				return;
			}
			if (secondsDelay > 0) {
				player.sendTitle("", String.valueOf(secondsDelay));
				secondsDelay--;
				return;
			}
			if (message != null) player.sendTitle(message);
			player.teleport(location);
			getManager().addCompleted(player);
		}
	}
	
	@RequiredArgsConstructor
	private static class TeleportTask extends Task {
		private final Teleport tp;
		@Override
		public void onRun (int i) {
			if (tp.getEntries().isEmpty()) {
				return;
			}
			tp.forEach(TeleportRequest::onTimer);
			
			if (tp.completed.isEmpty()) return;
			
			for (String name : tp.completed) {
				tp.remove(name);
			}
			tp.completed.clear();
		}
	}
	
}
