package lord.core.command.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class NotIntException extends Exception {
	
	private int argNumber;
	
}
