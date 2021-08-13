package lord.core.util.logger;

import cn.nukkit.plugin.PluginLogger;
import lord.core.api.CoreApi;
import lord.core.mgrbase.LordPlugin;

public class LordLogger implements ILordLogger {
	
	private PluginLogger logger;
	
	private String prefix = "[]";
	
	private LordLogger (PluginLogger logger) {
		this.logger = logger;
	}
	
	public static ILordLogger get (Object obj) {
		return get(obj.getClass());
	}
	
	public static ILordLogger get (Class clazz) {
		return get(clazz.getSimpleName());
	}
	
	public static ILordLogger get (String prefix) {
		return get(prefix, CoreApi.getCore());
	}
	
	public static ILordLogger get (Object obj, LordPlugin plugin) {
		return get(obj.getClass(), plugin);
	}
	
	public static ILordLogger get (Class clazz, LordPlugin plugin) {
		return get(clazz.getSimpleName(), plugin);
	}
	
	public static ILordLogger get (String prefix, LordPlugin plugin) {
		LordLogger logger = new LordLogger(plugin.getLogger());
		logger.setPrefix(prefix);
		return logger;
	}
	
	@Override
	public void setPrefix (String prefix) {
		this.prefix = "[" + prefix + "] ";
	}
	
	@Override
	public void info (String mess) {
		this.logger.info(this.prefix + mess);
	}
	
	@Override
	public void error (String mess) {
		this.logger.error(this.prefix + mess);
	}
	
	@Override
	public void alert (String mess) {
		this.logger.alert(this.prefix + mess);
	}
	
	@Override
	public void warning (String mess) {
		this.logger.warning(this.prefix + mess);
	}
	
	@Override
	public void debug (String mess) {
		this.logger.debug(this.prefix + mess);
	}
	
	@Override
	public void critical (String mess) {
		this.logger.critical(this.prefix + mess);
	}
	
	@Override
	public void enabled () {
		this.info("Enabled");
	}
	
	@Override
	public void disabled () {
		this.info("Disabled");
	}
	
	@Override
	public void loaded () {
		this.info("Loaded");
	}
	
}
