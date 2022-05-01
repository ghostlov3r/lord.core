package lord.core.game.kit;

import beengine.util.config.Config;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KitsConfig extends Config {
	
	private boolean enableCommand = true;
	
	private boolean enableMessages = true;
	
	private String generalMessage = "Получен набор предметов ";
	
	private boolean saveTimes = true;
	
	private boolean createDefault = true;
	
}
