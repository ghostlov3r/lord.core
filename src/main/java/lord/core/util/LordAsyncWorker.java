package lord.core.util;

public class LordAsyncWorker extends Thread {
	
	private static int nextWorkerID = 1;
	
	private static int getNextWorkerID () {
		int id = nextWorkerID;
		nextWorkerID++;
		return id;
	}
	
	public LordAsyncWorker () {
		super("LordAsyncWorker #" + getNextWorkerID());
	}
	
	volatile boolean running;
	
	public void shutdown () {
		running = false;
	}
	
	@Override
	public void run () {
		running = true;
		
		while (running) {
		
		}
		
	}
}
