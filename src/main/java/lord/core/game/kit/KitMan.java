package lord.core.game.kit;

import lombok.Getter;
import lombok.var;
import lord.core.LordCore;
import lord.core.command.service.LordCommand;
import lord.core.mgrbase.manager.LordManFC;

import java.util.HashMap;

@Getter
public class KitMan extends LordManFC<Kit, KitsConfig, LordCore> {
	
	private boolean kitsLoaded = false;
	
	public KitMan () {
		super();
		prettyJson();
		this.getLogger().enabled();
	}
	
	@Override
	protected LordCommand cmdInternal () {
		return new KitCommand(this);
	}
	
	public boolean loadKits () {
		if (kitsLoaded) return false;
		loadAll();
		
		if (getEntries().isEmpty()) {
			if (getConfig().isCreateDefault()) {
				var kit = createDefault();
				this.add(kit);
				kit.save();
			}
		}
		logLoadedEntries();
		return true;
	}
	
	public Kit createDefault () { // TODO Доделать создание и сохранение дефолтного
		Kit kit = new Kit();
		
		kit.hours = 0;
		kit.minutes = 5;
		kit.times = new HashMap<>();
		
		kit.finup("example", this);
		return kit;
	}
	
}
