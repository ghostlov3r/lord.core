package lord.core.game.achieve;

import lombok.Getter;
import lord.core.mgrbase.entry.LordEntryF;

/**
 * Достижение игрока
 * Элемент менеджера AchieveMan
 * Наследованное свойство name служебное
 * @author ghostlov3r
 */
@Getter
public class Achieve extends LordEntryF<AchieveMan> {
	
	/** Название достижения */
	protected String achieveName;
	
	/** Текст (задание) */
	protected String achieveText;
	
}
