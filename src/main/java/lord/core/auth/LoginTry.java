package lord.core.auth;

import beengine.player.Player;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class LoginTry {
	
	public static int maxCount = 5;
	
	private Map<InetSocketAddress, Integer> ips = new HashMap<>();
	
	public void increment (Player player) {
		InetSocketAddress ip = player.session().address();
		if (this.ips.containsKey(ip)) {
			this.ips.put(ip, this.ips.get(ip) + 1);
		} else {
			this.ips.put(ip, 1);
		}
	}
	
	public boolean remove (Player player) {
		InetSocketAddress ip = player.session().address();
		this.ips.remove(ip);
		return this.ips.isEmpty();
	}
	
	public boolean isLimitReached (Player player) {
		InetSocketAddress ip = player.session().address();
		if (this.ips.containsKey(ip)) {
			return maxCount == this.ips.get(ip);
		}
		return false;
	}
	
}
