package lord.core.mgrbase.entry;

import lombok.var;
import lord.core.mgrbase.manager.LordManF;
import lord.core.util.file.AdvFile;

/**
 * Элемент HashMap в менеджере с папкой
 * @param <MgrF> Тип менеджера
 */
public abstract class LordEntryF<MgrF extends LordManF>
	extends LordEntry<MgrF> {
	
	private AdvFile getFile () {
		return getManager().getEntriesFolder().getFile(this.getName(), AdvFile.JSON);
	}
	
	/**
	 * Сохраняет элемент на диск
	 */
	public void save () {
		if (getManager().isPrettyJson()){
			getFile().writePrettyJson(this);
		} else {
			getFile().writeJson(this);
		}
	}
	
	/**
	 * Удаляет элемент с диска
	 */
	public void deleteFile () {
		var file = getFile();
		if (file.exists()) file.delete();
	}

}
