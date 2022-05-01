package lord.core.game;

import beengine.entity.util.Location;
import beengine.player.Player;
import beengine.scheduler.Scheduler;
import beengine.scheduler.Task;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lord.core.game.warp.Warp;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teleport {
	
	/** Имена телепортированных */ private List<String> completed = new ArrayList<>();
	private Map<Player, TeleportRequest> requests = new HashMap<>();
	
	public Teleport () {
		Scheduler.repeat(1, new TeleportTask(this));
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
		player.setTitleDuration(2, 20, 10);
		
		var request = new TeleportRequest(this, player, location, secondsDelay, message);
		this.requests.put(player, request);
	}
	
	public boolean requested (Player player) {
		return this.requested(player.name());
	}
	
	public boolean requested (String name) {
		return this.requests.containsKey(name);
	}
	
	private void addCompleted (Player player) {
		this.completed.add(player.name());
	}
	
	public void cancel (Player player) {
		this.requests.remove(player.name());
	}
	
	@AllArgsConstructor
	protected static class TeleportRequest {
		Teleport manager;
		private final Player player;
		private final Location location;
		private int secondsDelay;
		@Nullable
		private String message;
		
		private void onTimer () {
			if (!player.isOnline()) {
				manager.addCompleted(player);
				return;
			}
			if (secondsDelay > 0) {
				player.sendTitle("", String.valueOf(secondsDelay));
				secondsDelay--;
				return;
			}
			if (message != null) player.sendTitle(message);
			player.teleport(location);
			manager.addCompleted(player);
		}
	}
	
	@RequiredArgsConstructor
	private static class TeleportTask extends Task {
		private final Teleport tp;
		@Override
		public void run () {
			if (tp.requests.isEmpty()) {
				return;
			}
			tp.requests.values().forEach(TeleportRequest::onTimer);
			
			if (tp.completed.isEmpty()) return;
			
			for (String name : tp.completed) {
				tp.requests.remove(name);
			}
			tp.completed.clear();
		}
	}
	
}
