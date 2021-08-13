package lord.core.listener.manager;

import cn.nukkit.Server;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;
import lord.core.listener.service.EvH;

// todo
public class ReflexBasedManager extends PluginManager {
	
	public ReflexBasedManager (Server server, SimpleCommandMap commandMap) {
		super(server, commandMap);
	}
	
	@Override
	public void registerEvents (Listener listener, Plugin plugin) {
		if ( !(listener instanceof EvH) ) {
			// Log
			return;
		}
	}
	
	@Override
	public void callEvent (Event event) {
		// ...
	}
}
