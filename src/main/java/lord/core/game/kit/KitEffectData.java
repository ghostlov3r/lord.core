package lord.core.game.kit;

import lombok.Getter;
import lombok.var;

/**
 * Информация об эффекте из набора
 */
@Getter
public class KitEffectData {
	
	/** ID эффекта */
	private int id;
	
	/** Длительность */
	private int seconds;
	
	/** Сила */
	private int level;
	
	public static KitEffectData create (int id) {
		return create(id, 10);
	}
	
	public static KitEffectData create (int id, int seconds) {
		return create(id, seconds, 1);
	}
	
	public static KitEffectData create (int id, int seconds, int level) {
		var data = new KitEffectData();
		data.id = id;
		data.seconds = seconds;
		data.level = level;
		return data;
	}
	
}
