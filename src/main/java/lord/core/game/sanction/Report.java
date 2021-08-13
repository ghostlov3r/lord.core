package lord.core.game.sanction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lord.core.mgrbase.entry.LordEntryF;

/**
 * Элемент ReportManager - менеджера репортов
 * Информация об отправленной жалобе
 * @author ghostlov3r
 */
@Getter @Builder @NoArgsConstructor
public class Report extends LordEntryF<ReportManager> {
	
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
	
}
