package lord.core.game.sanction;

import lombok.var;
import lord.core.LordCore;
import lord.core.mgrbase.manager.LordManF;

public class ReportManager extends LordManF<Report, LordCore> {
	
	public ReportManager (Sanction sanction) {
		super(sanction.getFolder().getChild("reports"));
	}
	
	// todo
	private String calcNextFileName () {
		return "";
	}
	
	// todo команда репорт и формы
	
	private Report createEntry (String target, String sender, String ruleID, String description) {
		var entry = Report.builder()
						  .targetName(target)
						  .senderName(sender)
						  .ruleID(ruleID)
						  .description(description)
						  .sentTime(System.currentTimeMillis())
						  .build();
		entry.finup(calcNextFileName(), this);
		return entry;
	}

}
