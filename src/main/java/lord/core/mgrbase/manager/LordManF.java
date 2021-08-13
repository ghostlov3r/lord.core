package lord.core.mgrbase.manager;

import lombok.Getter;
import lombok.Setter;
import lombok.var;
import lord.core.mgrbase.entry.LordEntryF;
import lord.core.mgrbase.LordPlugin;
import lord.core.util.file.AdvFile;
import lord.core.util.file.AdvFolder;

import java.util.ArrayList;

/**
 * База для менеджеров, содержащих Map и использующих папку
 * @param <EntryF> Тип значений Map
 * @param <Plug> Тип плагина-владельца
 */
@Getter
public abstract class LordManF<EntryF extends LordEntryF, Plug extends LordPlugin>
	extends LordMan<EntryF, Plug> {
	
	/** Директория менеджера */
	private AdvFolder folder;
	
	/** Директория элементов */
	private AdvFolder entriesFolder;
	
	/** Сериализация с табуляцией */
	@Setter
	private boolean prettyJson = false;
	
	/**
	 * По умолчанию папка на основе имени класса
	 */
	public LordManF () {
		this("");
	}
	
	/**
	 * @param folder Имя папки менеджера в папке плагина
	 */
	public LordManF (String folder) {
		super();
		
		if (folder == null || folder.equals("")) {
			folder = getManagerName();
		}
		
		this.folder = getPlugin().getFolder().mkdirChild(folder);
		this.entriesFolder = this.folder.mkdirChild("entries");
	}
	
	/**
	 * @param folder Папка менеджера
	 */
	public LordManF (AdvFolder folder) {
		super();
		
		folder.mkdirIfNot();
		
		this.folder = folder;
		entriesFolder = folder.mkdirChild("entries");
	}
	
	/**
	 * Json с табуляцией
	 */
	public void prettyJson() {
		this.prettyJson = true;
	}
	
	/**
	 * Выводит для каждого элемента в консоль Loaded: name
	 */
	public void logLoadedEntries () {
		this.forEach(entry -> {
			this.getLogger().info("Loaded: " + entry.getName());
		});
	}
	
	/**
	 * Загружает все элементы в буфер и возвращает его
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<EntryF> justLoadAll () {
		var entries = new ArrayList<EntryF>();
		this.getEntriesFolder().getAllFiles(AdvFile.JSON).forEach(file -> {
			var entry = file.readJson(this.getEntryClass());
			if (entry != null) {
				entry.finup(file.getName(), this);
				entries.add(entry);
			} else {
				this.getLogger().error("Deserialization failed for file: " + file.getName());
			}
		});
		return entries;
	}
	
	/**
	 * Загружает все элементы в Map
	 */
	public void loadAll () {
		this.justLoadAll().forEach(this::add);
	}
	
	/**
	 * Десериализует элемент и возвращает его
	 */
	@SuppressWarnings("unchecked")
	public EntryF justLoad (String name) {
		var file = this.getEntriesFolder().getFile(name, AdvFile.JSON);
		if (!file.exists()) {
			return null;
		}
		var entry = file.readJson(this.getEntryClass());
		if (entry != null) {
			entry.finup(name,  this);
		}
		return entry;
	}
	
	/**
	 * Загружает элемент в Map
	 */
	public EntryF loadToMap (String name) {
		var entry = this.justLoad(name);
		if (entry != null) {
			this.add(entry);
		}
		return entry;
	}
	
	/**
	 * Утилитный метод
	 * Рассчитывает имя в формате Int ID Increment
	 * Сначала пробует "1". Если такое есть, пробует "2"
	 */
	@Override
	public String nextIntName () {
		int id = 1;
		
		var names = new ArrayList<String>();
		folder.getAllFiles("json").forEach(file -> names.add(file.getBaseName()));
		
		while (true) {
			String newName = String.valueOf(id);
			if (names.contains(newName) || getEntries().containsKey(newName)) {
				id++;
			} else {
				return newName;
			}
		}
	}
	
}
