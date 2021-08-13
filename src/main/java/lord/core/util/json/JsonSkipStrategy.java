package lord.core.util.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Стратегия Gson для пропуска полей с аннотацией JsonSkip
 */
public class JsonSkipStrategy implements ExclusionStrategy {
	
	private Class<JsonSkip> annotation = JsonSkip.class;
	
	@Override
	public boolean shouldSkipField (FieldAttributes fieldAttributes) {
		return fieldAttributes.getAnnotation(this.annotation) != null;
	}
	
	@Override
	public boolean shouldSkipClass (Class<?> aClass) {
		return false;
	}
}
