package lord.core.game.auth;

import cn.nukkit.Player;

import java.util.HashMap;
import java.util.Map;

public class LoginTry {
	
	public static int maxCount = 5;
	
	private Map<String, Integer> ips = new HashMap<>();
	
	public void increment (Player player) {
		String ip = player.getAddress();
		if (this.ips.containsKey(ip)) {
			this.ips.put(ip, this.ips.get(ip) + 1);
		} else {
			this.ips.put(ip, 1);
		}
	}
	
	public boolean remove (Player player) {
		String ip = player.getAddress();
		this.ips.remove(ip);
		return this.ips.isEmpty();
	}
	
	public boolean isLimitReached (Player player) {
		String ip = player.getAddress();
		if (this.ips.containsKey(ip)) {
			return maxCount == this.ips.get(ip);
		}
		return false;
	}
	
}
