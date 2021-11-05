package lord.core.game.sanction;

import dev.ghostlov3r.common.DiskEntry;
import dev.ghostlov3r.common.DiskMap;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Элемент ReportManager - менеджера репортов
 * Информация об отправленной жалобе
 * @author ghostlov3r
 */
@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class Report extends DiskEntry<String> {
	
	/** ID правила, которое нарушила цель */
	private String ruleID;
	
	/** Имя отправителя жалобы */
	private String senderName;
	
	/** Имя нарушителя */
	private String targetName;
	
	/** Описание жалобы */
	private String description;
	
	/** Время отправления жалобы */
	private long sentTime;

	public Report(DiskMap<String, ?> map, String key) {
		super(map, key);
	}
}
