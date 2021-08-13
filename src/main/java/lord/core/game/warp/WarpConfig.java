package lord.core.game.warp;

import lombok.Getter;
import lord.core.mgrbase.entry.LordConfig;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WarpConfig extends LordConfig<WarpMan> {

	/** Имена варпов, которые загружаются в мапу сразу при включении */
	private List<String> forceLoadNames = new ArrayList<>();
	
}
