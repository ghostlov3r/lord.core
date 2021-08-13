package lord.core.scoreboard;

import lombok.Getter;
import lord.core.gamer.Gamer;
import lord.core.scoreboard.packet.RemoveObjectivePacket;
import lord.core.scoreboard.packet.ScoreInfo;
import lord.core.scoreboard.packet.SetDisplayObjectivePacket;
import lord.core.scoreboard.packet.SetScorePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scoreboard {
	
	/** Показывается ли сейчас игроку */
	@Getter
	private boolean showing;
	
	@Getter
	private final Gamer player;
	
	private HashMap<Integer, ScoreLine> lines = new HashMap<>();
	
	public Scoreboard (Gamer player) {
		this.player = player;
	}
	
	/** Установка линии */
	public void set (int line, String text) {
		ScoreLine scoreLine = this.lines.get(line);
		
		if (scoreLine == null) {
			scoreLine = new ScoreLine(line, text);
			this.lines.put(line, scoreLine);
		} else {
			scoreLine.setText(text);
			scoreLine.setDirty(true);
		}
	}
	
	/** Синхронизация с клиентом */
	public void update () {
		List<ScoreInfo> scores = new ArrayList<>();
		List<ScoreInfo> remove = new ArrayList<>();
		
		for (ScoreLine line : this.lines.values()) {
			if (line.isDirty()) {
				if (!line.isNew())
					remove.add(line.getRemoveInfo());
				scores.add(line.getSetInfo());
			}
		}
		
		if (!remove.isEmpty()) {
			SetScorePacket packet = new SetScorePacket();
			packet.type = SetScorePacket.TYPE_REMOVE;
			packet.putScorePacketInfos(remove);
			this.player.dataPacket(packet);
		}
		if (!scores.isEmpty()) {
			SetScorePacket packet = new SetScorePacket();
			packet.type = SetScorePacket.TYPE_CHANGE;
			packet.putScorePacketInfos(scores);
			this.player.dataPacket(packet);
		}
	}
	
	public void delete (int line) {
	
	}
	
	/** Инициализация скорборда у клиента */
	public void show () {
		if (this.showing) return;
		SetDisplayObjectivePacket packet = new SetDisplayObjectivePacket();
		packet.criteriaName = "dummy";
		packet.displayName = player.getCore().config().getBoldName();
		packet.displaySlot = "sidebar";
		packet.objectiveName = "objective"; // check
		packet.sortOrder = 0;
		this.player.dataPacket(packet);
	}
	
	/** Убирает скорборд у клиента */
	public void hide () {
		if (!this.showing) return;
		RemoveObjectivePacket packet = new RemoveObjectivePacket();
		packet.objectiveName = "objective"; // check
		this.player.dataPacket(packet);
	}
	
}
