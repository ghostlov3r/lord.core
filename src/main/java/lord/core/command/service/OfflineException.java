package lord.core.command.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class OfflineException extends Exception {
	
	private String playerName;
	
}
