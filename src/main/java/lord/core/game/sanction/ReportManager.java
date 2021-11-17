package lord.core.game.sanction;

import dev.ghostlov3r.beengine.utils.DiskMap;
import lord.core.Lord;

public class ReportManager extends DiskMap<String, Report> {
	
	// todo
	private String calcNextFileName () {
		return "";
	}

	public ReportManager () {
		super(Lord.instance.dataPath().resolve("reports"), Report.class);

	}
	
	// todo команда репорт и формы
	
	private Report createEntry (String target, String sender, String ruleID, String description) {
		return new Report(this, calcNextFileName())
						  .targetName(target)
						  .senderName(sender)
						  .ruleID(ruleID)
						  .description(description)
						  .sentTime(System.currentTimeMillis());
	}

}
