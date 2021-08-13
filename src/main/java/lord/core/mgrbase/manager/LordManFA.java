package lord.core.mgrbase.manager;

import lombok.Getter;
import lombok.var;
import lord.core.mgrbase.entry.LordEntryF;
import lord.core.mgrbase.LordPlugin;
import lord.core.util.file.AdvFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Менеджер, который автоматически подгружает элементы при отсутствии в мапе
 * @param <EntryF> Тип элементов
 * @param <Plug> Тип плагина-владельца
 * @author ghostlov3r
 */
@Getter
public abstract class LordManFA<EntryF extends LordEntryF, Plug extends LordPlugin>
	extends LordManF<EntryF, Plug> {
	
	/** Имя вхождений, которые были проверены на отсутствие в папке entries */
	private final List<String> absentNames = new ArrayList<>();
	
	public LordManFA () {
		super();
	}
	
	public LordManFA (String folder) {
		super(folder);
	}
	
	public LordManFA (AdvFolder folder) {
		super(folder);
	}
	
	/**
	 * Возвращает элемент по имени.
	 * Если элемента нету, то проверяется папка.
	 * Если элемент найден, он загружается в мапу и возвращается,
	 * а иначе заносится в список отсутствующих.
	 * @param name Имя элемента
	 */
	@Override
	public EntryF get (String name) {
		var entry = super.get(name);
		if (entry != null) {
			return entry;
		}
		if (absentNames.contains(name)) {
			return null;
		}
		entry = justLoad(name);
		if (entry == null) {
			absentNames.add(name);
			return null;
		}
		super.add(entry);
		return entry;
	}
	
	/**
	 * Добавлет элемент в мапу,
	 * а также удаляет его из отсутствующих
	 * @param entry Элемент менеджера
	 */
	@Override
	public void add (EntryF entry) {
		absentNames.remove(entry.getName());
		super.add(entry);
	}
	
}
