package lord.core.mgrbase.manager;

import lombok.Getter;
import lord.core.mgrbase.entry.LordConfig;
import lord.core.mgrbase.entry.LordEntryF;
import lord.core.mgrbase.LordPlugin;
import lord.core.util.Util;
import lord.core.util.file.AdvFile;
import lord.core.util.file.AdvFolder;

/**
 * База для менеджеров, содержащих Map и использующих папку + конфиг
 * @param <Entry> Тип элементов менеджера
 * @param <Config> Тип конфигурации менеджера
 * @param <Plug> Тип плагина-владельца
 */
@Getter
public abstract class LordManFC<Entry extends LordEntryF, Plug extends LordPlugin, Config extends LordConfig>
	extends LordManF<Entry, Plug> {
	
	public static final String CONFIG_FILE_NAME = "config";
	
	/** Конфигурация менеджера */
	private Config config;
	
	/** Класс конфига менеджера */
	private Class<Config> configClass;
	
	/** Файл конфига менеджера */
	private AdvFile configFile;
	
	/**
	 * По умолчанию папка на основе имени класса
	 */
	public LordManFC () {
		this("");
	}
	
	/**
	 * @param folder Имя папки менеджера
	 */
	public LordManFC (String folder) {
		super(folder);
		init();
	}
	
	/**
	 * @param folder Папка менеджера
	 */
	public LordManFC (AdvFolder folder) {
		super(folder);
		init();
	}
	
	@SuppressWarnings("unchecked")
	private void init () {
		configClass = (Class<Config>) Util.superGenericClass(this, 3);
		configFile = getFolder().getFile(CONFIG_FILE_NAME, AdvFile.JSON);
		
		if (configFile.exists()) {
			loadConfig();
		} else {
			createConfig();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadConfig () {
		config = configFile.readJson(configClass);
		if (config != null) {
			config.finup(CONFIG_FILE_NAME,  this);
		} else {
			getLogger().error("Deserialization of config FAILED");
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createConfig () {
		config = Util.newInstance(configClass);
		if (config != null) {
			config.finup(CONFIG_FILE_NAME,  this);
			config.save();
		} else {
			getLogger().error("Instantiation of config FAILED");
		}
	}
	
}
