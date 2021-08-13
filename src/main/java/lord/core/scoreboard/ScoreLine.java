package lord.core.scoreboard;

import lombok.Getter;
import lombok.Setter;
import lord.core.scoreboard.packet.ScoreInfo;

public class ScoreLine {
	
	@Getter
	private final int number;
	
	@Getter @Setter
	private String text;
	
	/** Требуется ли обновление на клиенте */
	@Getter @Setter
	private boolean dirty;
	
	/** Если да, то удаление старых линий не требуется */
	@Getter @Setter
	private boolean isNew;
	
	public ScoreLine (int number, String text) {
		this.number = number;
		this.text = text;
		this.isNew = true;
		this.dirty = true;
	}
	
	/** После вызова метода линия считается синхронизированной с клиентом */
	public ScoreInfo getSetInfo() {
		this.setDirty(false);
		this.setNew(false);
		return new ScoreInfo(this.number, "objective", this.number, this.text);
	}
		
	public ScoreInfo getRemoveInfo() {
		return new ScoreInfo(this.number, "objective", this.number);
	}
}
