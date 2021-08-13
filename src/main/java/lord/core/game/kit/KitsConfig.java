package lord.core.game.kit;

import lombok.Getter;
import lombok.Setter;
import lord.core.mgrbase.entry.LordConfig;

@Getter @Setter
public class KitsConfig extends LordConfig<KitMan> {
	
	private boolean enableCommand = true;
	
	private boolean enableMessages = true;
	
	private String generalMessage = "Получен набор предметов ";
	
	private boolean saveTimes = true;
	
	private boolean createDefault = true;
	
}
