package lord.core.mgrbase.entry;

import lombok.Getter;
import lord.core.mgrbase.manager.LordMan;
import lord.core.util.json.JsonSkip;

/**
 * Элемент HashMap в LordManager
 * @param <Mgr> Тип менеджера
 */
@Getter
public abstract class LordEntry<Mgr extends LordMan> {
	
	/** Имя элемента */
	@JsonSkip
	protected String name;
	
	/** Менеджер этого элемента */
	@JsonSkip
	private Mgr manager;
	
	/**
	 * Инициализация JsonSkip свойств
	 * @param name Имя элемента
	 * @param manager Менеджер элемента
	 */
	public void finup (String name, Mgr manager) {
		this.name = name;
		this.manager = manager;
	}
	
	/**
	 * Убирает из менеджера этот элемент
	 */
	@SuppressWarnings("unchecked")
	public void removeFromMap () {
		this.manager.remove(this);
	}
	
}
