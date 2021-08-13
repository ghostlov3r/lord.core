package lord.core.game.sanction;

import lombok.*;
import lord.core.mgrbase.entry.LordEntryF;

import java.util.Date;

@Getter @Builder @NoArgsConstructor
public class BanEntry extends LordEntryF<Sanction> {
	
	/** Unix-время разблок. или 0-Навсегда */ @Setter private long until;
	/** Имя блокировщика                   */         private String blockerName;
	/** ID правила из причины бана         */         private String ruleID;
	
	/**
	 * @return True, если игрок забанен навсегда
	 */
	public boolean isForever () {
		return this.until == 0;
	}
	
	/**
	 * @return Дата разблокировки в дебильном формате
	 */
	public String getUnbanDate () {
		return new Date(this.until).toGMTString();
	}
	
	/**
	 * @return Причина бана
	 */
	public BanReason getReason () {
		return getManager().getReasons().get(this.ruleID);
	}
	
	/**
	 * @return Сообщение для кика игрока
	 */
	public String generateMessage () {
		val message = new StringBuilder()
			.append(ACCOUNT_BLOCKED)
			.append(this.blockerName)
			.append(NEXT_LINE);
		if (this.until == 0) {
			message.append(FOREVER);
		} else {
			message.append(UNTIL);
			message.append(this.getUnbanDate());
		}
		message.append(NEXT_LINE)
			   .append(REASON)
			   .append(this.ruleID)
			   .append(" ");
		val reason = getReason();
		if (reason == null) {
			message.append(UNKNOWN);
		} else {
			message.append(reason.getText());
		}
		return message.toString();
	}
	
	public static final String NEXT_LINE = System.lineSeparator();
	public static final String ACCOUNT_BLOCKED = "Этот аккаунт заблокирован игроком ";
	public static final String FOREVER = " навсегда";
	public static final String UNTIL = " до ";
	public static final String REASON = "за нарушение П.";
	public static final String UNKNOWN = "Неизвестно";
	
	
	public static int daysToHours (int days) {
		return days * 24;
	}
	
	public static long daysToMillis (int days) {
		return hoursToMillis(daysToHours(days));
	}
	
	public static long hoursToMillis (int hours) {
		return hours * 60 * 60 * 1000;
	}
}
