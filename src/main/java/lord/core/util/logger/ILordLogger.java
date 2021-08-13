package lord.core.util.logger;

public interface ILordLogger {
	
	public void setPrefix (String prefix);
	
	public void info (String mess);
	
	public void error (String mess);
	
	public void alert (String mess);
	
	public void warning (String mess);
	
	public void debug (String mess);
	
	public void critical (String mess);
	
	void enabled();
	
	void disabled();
	
	void loaded();
	
}
