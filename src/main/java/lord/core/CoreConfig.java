package lord.core;

import lombok.Getter;
import lord.core.util.json.LordJson;

@Getter
public class CoreConfig implements LordJson {
	
	/** Период сохранения */
	private int savePeriodSec = 60;
	
	/** Время до выключения */
	private int shutdownDelayMin = 60;
	
	/** Период обновления скорборда */
	private int scoreUpdatePeriod = 6;
	
	/** Использовать ли лог пакетов в консоль */
	private boolean packetLoggerEnabled = false;
	
	private boolean clearNukkitCommands = true;
	
	private String nameLN = "Server" + System.lineSeparator(); // "§l§8]+[ §9NEKRAFT §8]+[§r\n"
	private String prefix = "Server "; // §8[§9NEKRAFT§8]
	private String boldName = "Server"; // §l§8[ §9NEKRAFT §8]
	private String longName = "Server"; // §l§8]+[ §9NEKRAFT §8]+[§r
	
	
	@Override
	public void save () {
		// don't need this
	}
}
