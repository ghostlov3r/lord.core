package lord.core.mgrbase.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.var;
import lord.core.LordCore;
import lord.core.api.CoreApi;
import lord.core.command.service.CmdArgs;
import lord.core.command.service.LordCmdException;
import lord.core.command.service.LordCommand;
import lord.core.gamer.Gamer;
import lord.core.mgrbase.LordManForms;
import lord.core.mgrbase.LordPlugin;
import lord.core.mgrbase.entry.LordEntry;
import lord.core.util.Util;
import lord.core.util.logger.ILordLogger;
import lord.core.util.logger.LordLogger;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * База для менеджеров, содержащих Map
 * @param <Entry> Тип значений Map
 * @param <Plug> Тип плагина-владельца
 */
@Getter
public abstract class LordMan<Entry extends LordEntry, Plug extends LordPlugin> {
	
	private String managerName;
	
	private LordCore core;
	
	/** Плагин-владелец менеджера */
	private Plug plugin;
	
	private ILordLogger logger;
	
	/** Класс хранимых в Map значений */
	private Class<Entry> entryClass;
	
	/** Элементы менеджера */
	private Map<String, Entry> entries;
	
	@Nullable
	private LordCommand command;
	
	@Nullable @Setter
	private LordManForms forms;
	
	@SuppressWarnings("unchecked")
	public LordMan () {
		core = CoreApi.getCore();
		
		var pluginClass = Util.superGenericClass(this, 2);
		plugin = (Plug) Util.invoke(pluginClass, "getInstance");
		if (plugin == null) {
			core.getLogger().critical("Unable to get plugin instance using getInstance! Shutdown!");
			core.getServer().shutdown();
		}
		
		this.logger = LordLogger.get(this, plugin);
		this.entries = new HashMap<>();
		this.entryClass = (Class<Entry>) Util.superGenericClass(this, 1);
		
		managerName = updateManagerName();
	}
	
	/**
	 * Возвращает элемент менеджера по имени
	 * @return Null, если элемента нет
	 */
	@Nullable
	public Entry get (String name) {
		return this.entries.get(name);
	}
	
	/**
	 * Добавляет элемент в HashMap
	 * @param entry Элемент менеджера
	 */
	public void add (Entry entry) {
		this.entries.put(entry.getName(), entry);
	}
	
	/**
	 * @param name Имя элемента
	 * @return True, если элемент есть в HashMap
	 */
	public boolean exists (String name) {
		return this.entries.containsKey(name);
	}
	
	/**
	 * @param entry Элемент менеджера
	 * @return True, если элемент есть в HashMap
	 */
	public boolean exists (Entry entry) {
		return this.entries.containsValue(entry);
	}
	
	/**
	 * Удаляет элемент из HashMap
	 * @param name Имя элемента
	 */
	public void remove (String name) {
		this.entries.remove(name);
	}
	
	/**
	 * Удаляет элемент из HashMap
	 * @param entry Элемент менеджера
	 */
	public void remove (Entry entry) {
		this.entries.remove(entry.getName());
	}
	
	/**
	 * Для каждого элемента Map
	 */
	public void forEach (Consumer<Entry> action) {
		if (action == null) return;
		for (Entry entry : this.getEntries().values()) {
			action.accept(entry);
		}
	}
	
	/**
	 * Утилитный метод
	 * Рассчитывает имя в формате Int ID Increment
	 * Сначала пробует "1". Если такое есть, пробует "2"
	 */
	public String nextIntName () {
		int id = 1;
		while (true) {
			String newName = String.valueOf(id);
			if (entries.containsKey(newName)) {
				id++;
			} else {
				return newName;
			}
		}
	}
	
	public String updateManagerName () {
		String name = this.getClass().getSimpleName();
		name = name.replaceAll("Manager", "");
		name = name.replaceAll("Man", "");
		if (name.charAt(name.length() - 1) != 's') {
			name = name.concat("s");
		}
		this.managerName = name.toLowerCase();
		return this.managerName;
	}
	
	public void enableCommand () {
		enableCommand(managerName);
	}
	
	public void enableCommand (String name) {
		enableCommand(name, "Lord " + managerName);
	}
	
	public void enableCommand (String name, String desc) {
		if (command != null) {
			if (command.getName().equals(name)) {
				command.setDescription(desc);
				return;
			}
			disableCommand();
		}
		command = registerCommand(name, desc);
		getLogger().info("Command enabled: " + name);
	}
	
	/**
	 * Убирает регистрацию команды
	 */
	public void disableCommand () {
		if (command == null) {
			return;
		}
		command.unregister(core.getServer().getCommandMap());
		command = null;
		getLogger().info("Command disabled: " + command.getName());
	}
	
	private LordCommand registerCommand (String name, String description) {
		var command = new LordCommand(name) {
			
			@Override
			public boolean handle (Gamer player, CmdArgs args) throws LordCmdException {
				if (args.count() == 0) {
					sendMainForm(player);
				} else {
					onCommand(player, args);
				}
				return true;
			}
			
		};
		command.setDescription(description);
		return command;
	}
	
	/**
	 * Выполняется, если в команду переданы аргументы
	 */
	public void onCommand (Gamer gamer, CmdArgs args) {
		// aka abstract
	}
	
	/**
	 * Если forms == null, отправится сообщение
	 */
	public void sendMainForm (Gamer gamer) {
		if (forms == null) {
			gamer.sendMessage("Lord " + managerName);
			return;
		}
		forms.mainForm(gamer);
	}
	
}
