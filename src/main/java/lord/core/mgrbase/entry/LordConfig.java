package lord.core.mgrbase.entry;

import lord.core.mgrbase.manager.LordManFC;

/**
 * Конфигурация менеджера
 * @param <MgrFC> Тип менеджера
 */
public abstract class LordConfig<MgrFC extends LordManFC>
	extends LordEntryF<MgrFC>{
	
	/**
	 * Сохраняет конфигурацию на диск
	 */
	@Override
	public void save () {
		this.getManager().getConfigFile().writePrettyJson(this);
	}
	
}
