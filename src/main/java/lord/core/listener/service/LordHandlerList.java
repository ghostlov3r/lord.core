package lord.core.listener.service;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import lombok.var;

import java.util.ArrayList;

public class LordHandlerList<Ev extends Event> extends HandlerList {
	
	public EvH<Ev>[] handlers;
	
	@SuppressWarnings("unchecked")
	public void call (Event event) {
		for (EvH<Ev> handler : handlers) {
			handler.handle((Ev) event);
		}
	}
	
	// todo redo with prior
	public void add (EvH<Ev> handler) {
		var list = new ArrayList<EvH<Ev>>();
		for (EvH<Ev> hr : handlers) {
			list.add(hr);
		}
		list.add(handler);
		// LordCore.log.info("Registered ");
	}
	
}
