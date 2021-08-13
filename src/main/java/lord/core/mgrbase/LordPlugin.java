package lord.core.mgrbase;

import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import lord.core.util.file.AdvFolder;

/**
 * Основа для Lord-плагина
 * @author ghostlov3r
 */
public abstract class LordPlugin extends PluginBase {
	
	/* Должен иметь static getInstance */
	
	/** Папка данных плагина */
	@Getter
	private AdvFolder folder;
	
	@Override
	public void onLoad () {
		super.onLoad();
		this.folder = AdvFolder.get(this.getDataFolder().getPath());
		this.folder.mkdirIfNot();
	}
}
