package lord.core.api;

import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.scheduler.Task;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;
import lord.core.LordCore;

@UtilityClass
public class TaskApi {
	
	private LordCore lordCore;
	
	@Getter @Accessors(fluent = true)
	private ServerScheduler scheduler;
	
	/** Инициализатор */
	private void onEnable (LordCore core) {
		lordCore = core;
		scheduler = core.getServer().getScheduler();
	}
	
	private int toTicks (double seconds) {
		return (int) (seconds * 20);
	}
	
	/**
	 * Отложенное задание
	 * @param delay Задержка в секундах
	 */
	public void delay (double delay, Runnable task) {
		scheduler().scheduleDelayedTask(lordCore, task, toTicks(delay));
	}
	
	/**
	 * Отложенное задание
	 * @param delay Задержка в секундах
	 */
	public void delay (double delay, Task task) {
		scheduler().scheduleDelayedTask(task, toTicks(delay));
	}
	
	/**
	 * Повторяющееся задание
	 * @param period Период в секундах
	 */
	public void repeat (double period, Runnable task) {
		scheduler().scheduleRepeatingTask(lordCore, task, toTicks(period));
	}
	
	/**
	 * Повторяющееся задание
	 * @param period Период в секундах
	 */
	public void repeat (double period, Task task) {
		scheduler().scheduleRepeatingTask(task, toTicks(period));
	}
	
	/**
	 * Повторяющееся задание с начальной задержкой
	 * @param delay Задержка в секундах
	 * @param period Период в секундах
	 */
	public void delayedRepeat (double delay, double period, Runnable task) {
		scheduler().scheduleDelayedRepeatingTask(lordCore, task, toTicks(delay), toTicks(period));
	}
	
	/**
	 * Повторяющееся задание с начальной задержкой
	 * @param delay Задержка в секундах
	 * @param period Период в секундах
	 */
	public void delayedRepeat (double delay, double period, Task task) {
		scheduler().scheduleDelayedRepeatingTask(task, toTicks(delay), toTicks(period));
	}
	
}
