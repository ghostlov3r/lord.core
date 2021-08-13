package lord.core.api;

import cn.nukkit.Player;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.Plugin;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lord.core.LordCore;
import lord.core.command.service.LordCommand;
import lord.core.gamer.Gamer;
import lord.core.gamer.GamerMan;
import lord.core.scoreboard.Scoreboard;

import java.util.function.Consumer;

/** Методы, которые доступны для использования другим плагинам */
@UtilityClass
public class CoreApi {
	
	@Getter
	private LordCore core;
	
	/** Инициализатор */
	private void onEnable (LordCore lordCore) {
		core = lordCore;
	}
	
	/** Регистрирует прослушиватели */
	@Deprecated
	public void listeners (Listener ...listeners) {
		listeners(core, listeners);
	}
	
	/** Регистрирует прослушиватели */
	@Deprecated
	public void listeners (Plugin plugin, Listener ...listeners) {
		for (Listener listener : listeners) {
			if (listener != null) {
				core.pluginManager().registerEvents(listener, plugin);
			}
		}
	}
	
	/** Регистрирует Команды */
	@Deprecated
	public void commands (LordCommand ...commands) {
		for (LordCommand command : commands) {
			if (command != null) {
				core.getServer().getCommandMap().register(command.getName(), command);
			}
		}
	}
	
	/** Устанавливает класс данных игрока */
	public void gamerDataMgr (GamerMan mgr) {
		core.gamerMan(mgr);
	}
	
	/** Изменения линий скорборда при обновлении */
	public void onScoreUpdate (Consumer<Scoreboard> actions) {
		core.scoreMan().onUpdate(actions);
	}
	
	/** Установка линий скорборда при входе на сервер */
	public void onScoreShow (Consumer<Scoreboard> actions) {
		core.scoreMan().onShow(actions);
	}
	
	/** Полное отключение скорборда */
	public void disableScore () {
		core.scoreMan().disable();
	}
	
	/** Сообщение авторизованным игрокам */
	public void broadcast (String message) {
		for (Player player : LordCore.server.getOnlinePlayers().values()) {
			if (((Gamer)player).isAuthorized()) player.sendMessage(message);
		}
	}
	
	/** Сообщение всем игрокам */
	public void broadcastAll (String message) {
		for (Player player : LordCore.server.getOnlinePlayers().values()) {
			player.sendMessage(message);
		}
	}

}


// OLD

/* for (Method method : listener.getClass().getMethods()) {
			if (method.getParameterCount() == 1 && method.getName().equals("handle")) {
				Class eventClass = method.getParameterTypes()[0];
				if (Event.class.isAssignableFrom(eventClass)) {
					LordCore.pluginManager.registerListener(eventClass, listener);
				}
			}
		}*/