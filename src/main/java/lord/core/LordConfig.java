package lord.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.ghostlov3r.beengine.utils.config.Config;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class LordConfig extends Config {
	
	/** Время до выключения */
	private int shutdownDelayMin = 60;
	
	private boolean clearNukkitCommands = true;
	
	private String nameLN = "Server" + System.lineSeparator(); // "§l§8]+[ §9NEKRAFT §8]+[§r\n"
	private String prefix = "Server "; // §8[§9NEKRAFT§8]
	private String boldName = "Server"; // §l§8[ §9NEKRAFT §8]
	private String longName = "Server"; // §l§8]+[ §9NEKRAFT §8]+[§r

	List<String> vanillaCommands = new ArrayList<>();

	public long keepAuthorized = 1;
	public TimeUnit keepAuthorizedUnit = TimeUnit.DAYS;

	public String authWorld = "auth";
}
