package lord.core.game.achieve;

import beengine.util.DiskEntry;
import beengine.util.DiskMap;
import lombok.Getter;

/**
 * Достижение игрока
 * Элемент менеджера AchieveMan
 * Наследованное свойство name служебное
 * @author ghostlov3r
 */
@Getter
public class Achieve extends DiskEntry<String> {
	
	/** Название достижения */
	protected String achieveName;
	
	/** Текст (задание) */
	protected String achieveText;

	public Achieve(DiskMap<String, ?> map, String key) {
		super(map, key);
	}
}
