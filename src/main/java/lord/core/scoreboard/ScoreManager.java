package lord.core.scoreboard;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lord.core.LordCore;
import lord.core.api.TaskApi;
import lord.core.util.Util;

import java.util.function.Consumer;

public class ScoreManager {
	
	@Getter private boolean enabled;
	
	@Setter private int updatePeriod;
	
	private ScoreTask currentTask;
	
	public ScoreManager (LordCore core) {
		this.updatePeriod = core.getConfig().getInt("scoreUpdatePeriod");
		this.enable();
	}
	
	@Getter @Setter @Accessors(fluent = true)
	private Consumer<Scoreboard> onShow = (score) -> {
		score.set(1, "Строка 1");
		score.set(3, "Строка 3");
		score.set(5, "Рандом: " + Util.getRandom().nextInt(200));
	};
	
	@Getter @Setter @Accessors(fluent = true)
	private Consumer<Scoreboard> onUpdate = (score) -> {
		score.set(5, "Рандом: " + Util.getRandom().nextInt(200)); // todo fix random
	};
	
	public void enable () {
		this.currentTask = new ScoreTask();
		TaskApi.repeat(this.updatePeriod, this.currentTask);
		this.enabled = true;
	}
	
	public void disable () {
		if (this.currentTask != null) {
			this.currentTask.cancel();
		}
		this.currentTask = null;
		this.enabled = false;
	}
	
}
