package lord.core.game.sanction;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Информация об отношении определенного игрока к репортам
 * @author ghostlov3r
 */
@Getter @Setter
public class ReportData {
	
	/** Имена-ID отправленных репортов */
	@Nullable
	private List<String> sentIDs;
	
	/** Имена-ID пришедших репортов */
	@Nullable private List<String> inboxIDs;
	
}
