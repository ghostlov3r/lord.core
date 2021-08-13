package lord.core.game.kit;

import lombok.Getter;
import lombok.var;

/**
 * Информация о зачаровании предмета из набора
 */
@Getter
public class KitItemEnchData {
	
	/** ID Зачарования */
	private int id;
	
	/** Уровень зачарования */
	private int level;
	
	public static KitItemEnchData create (int id) {
		return create(id, 1);
	}
	
	public static KitItemEnchData create (int id, int level) {
		var data = new KitItemEnchData();
		data.id = id;
		data.level = level;
		return data;
	}
	
}
