package lord.core.listener.service;

import cn.nukkit.event.Event;
import cn.nukkit.event.Listener;

/**
 * Lord-Слушатель должен реализовать этот интерфейс
 * @param <Ev> Тип прослушиваемого события
 * @author ghostlov3r
 */
public interface EvH<Ev extends Event> extends Listener {
	
	void handle (Ev ev);
	
}
