package lord.core.game.warp;

import beengine.util.config.Config;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WarpConfig extends Config {

	/** Имена варпов, которые загружаются в мапу сразу при включении */
	private List<String> forceLoadNames = new ArrayList<>();
	
}
