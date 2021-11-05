package lord.core.game.kit;

import dev.ghostlov3r.beengine.utils.config.Config;
import dev.ghostlov3r.common.DiskMap;
import lombok.Getter;
import lord.core.LordCore;

import java.util.HashMap;

@Getter
public class KitMan extends DiskMap<String, Kit> {
	
	private boolean kitsLoaded = false;
	private KitsConfig config;

	public KitMan () {
		super(LordCore.instance().dataPath().resolve("kits"), Kit.class);
		config = Config.loadFromDir(path().resolve("cfg"), KitsConfig.class);
	}

	/*@Override
	protected LordCommand cmdInternal () {
		return new KitCommand(this);
	}*/
	
	public boolean loadKits () {
		if (kitsLoaded) return false;
		loadAll();
		
		if (isEmpty()) {
			if (config.isCreateDefault()) {
				Kit kit = createDefault();
				this.put(kit.key(), kit);
				kit.save();
			}
		}
		return true;
	}
	
	public Kit createDefault () { // TODO Доделать создание и сохранение дефолтного
		Kit kit = new Kit(this, "example");
		
		kit.hours = 0;
		kit.minutes = 5;
		kit.times = new HashMap<>();
		
		return kit;
	}
	
}
