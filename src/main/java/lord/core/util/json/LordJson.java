package lord.core.util.json;

import lord.core.util.file.AdvFile;

/**
 * Реализующий класс подлежит Json-сериализации и десериализации
 */
public interface LordJson {
	
	/** Сериализует объект и сохраняет в файл */
	default void saveInternal (AdvFile file) {
		file.writeJson(this);
	}
	
	/** Красиво сериализует объект и сохраняет в файл */
	default void savePretty (AdvFile file) {
		file.writeJson(this);
	}
	
	/** Этот метод должен использовать saveInternal() */
	void save ();
	
}
