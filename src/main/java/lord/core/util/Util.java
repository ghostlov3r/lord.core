package lord.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.var;
import lord.core.LordCore;
import lord.core.util.json.JsonSkipStrategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Random;

@UtilityClass
public class Util {
	
	/** Генератор случайных чисел */
	@Getter
	private final Random random;
	
	/** Стандартный сериализатор */
	@Getter
	private final Gson gson;
	
	/** Сериализатор с табуляцией */
	@Getter
	private final Gson prettyGson;
	
	static {
		random = new Random();
		
		gson = new GsonBuilder()
			.setExclusionStrategies(new JsonSkipStrategy())
			// .serializeNulls()
			.create();
		
		prettyGson = new GsonBuilder()
			.setExclusionStrategies(new JsonSkipStrategy())
			// .serializeNulls()
			.setPrettyPrinting()
			.create();
	}
	
	public int toChunk (int coord) {
		return coord >> 4;
	}
	
	public int fromChunk (int coord) {
		return coord << 4;
	}
	
	public String firstUpperCase (String str) {
		if (str == null) return null;
		if (str.isEmpty()) return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public String upperNameToCamel (String name) {
		if (name == null) return null;
		if (name.isEmpty()) return name;
		var words = name.split("_");
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			result.append(i > 0
				? firstUpperCase(words[i])
				: words[i]);
		}
		return result.toString();
	}
	
	/** Возвращает значение статичного свойства */
	public Object getFieldValue (Class clazz, String field) {
		return getFieldValue(clazz, null, field);
	}
	
	/** Возвращает значение свойства */
	public Object getFieldValue (Object obj, String field) {
		return getFieldValue(obj.getClass(), obj, field);
	}
	
	/** Возвращает значение свойства */
	public Object getFieldValue (Class clazz, Object obj, String field) {
		Object object = null;
		try {
			object = clazz.getField(field).get(obj);
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	/** Ставит значение статичного свойства */
	public void setFieldValue (Class clazz, String field, Object value) {
		setFieldValue(clazz, null, field, value);
	}
	
	/** Ставит значение свойства */
	public void setFieldValue (Object obj, String field, Object value) {
		setFieldValue(obj.getClass(), obj, field, value);
	}
	
	/** Ставит значение свойства */
	public void setFieldValue (Class clazz, Object obj, String field, Object value) {
		try {
			clazz.getField(field).set(null, value);
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/** Возвращает типы дженериков, переданных в класс-родитель (Child extends Parent<Gen>) */
	public Type[] getSuperGenericTypes (Object obj) {
		return getSuperGenericTypes(obj.getClass());
	}
	
	/** Возвращает типы дженериков, переданных в класс-родитель (Child extends Parent<Gen>) */
	public Type[] getSuperGenericTypes (Class<?> clazz) {
		return ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
	}
	
	/** Возвращает строковые названия дженериков суперкласса (Child extends Parent<Gen>) */
	public String[] getSuperGenericNames (Object obj) {
		return getSuperGenericNames(obj.getClass());
	}
	
	/** Возвращает строковые названия дженериков суперкласса (Child extends Parent<Gen>) */
	public String[] getSuperGenericNames (Class<?> clazz) {
		var types = getSuperGenericTypes(clazz);
		String[] names = new String[types.length];
		
		if (types.length > 0) {
			for (int i = 0; i < types.length; i++) {
				names[i] = types[i].getTypeName();
			}
		}
		return names;
	}
	
	/** Возвращает классы дженериков суперкласса (Child extends Parent<Gen>) */
	@Deprecated
	public Class<?>[] getSuperGenericClasses (Object obj) {
		return getSuperGenericClasses(obj.getClass());
	}
	
	/** Возвращает классы дженериков суперкласса (Child extends Parent<Gen>) */
	@Deprecated
	public Class<?>[] getSuperGenericClasses (Class<?> clazz) {
		var names = getSuperGenericNames(clazz);
		Class<?>[] classes = new Class<?>[names.length];
		
		if (names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				try {
					classes[i] = Class.forName(names[i]);
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return classes;
	}
	
	/**
	 * Для вызова все дженерики должны быть заполнены реальными классами
	 * @param num Порядковый номер дженерика
	 * @return Класс дженерика, переданного в суперкласс
	 */
	public Class<?> superGenericClass (Object obj, int num) {
		return superGenericClass(obj.getClass(), num);
	}
	
	/**
	 * Для вызова все дженерики должны быть заполнены реальными классами
	 * @param num Порядковый номер дженерика
	 * @return Класс дженерика, переданного в суперкласс
	 */
	public Class<?> superGenericClass (Class<?> clazz, int num) {
		var generics = getSuperGenericClasses(clazz);
		if (generics == null) {
			LordCore.log.critical("Trying get generics of " + clazz.getSimpleName() + " returned null");
			LordCore.server.shutdown();
			return null;
		}
		try {
			return generics[num - 1];
		} catch (Exception e) {
			LordCore.log.critical("Array with generics of " + clazz.getSimpleName() + " not consists num " + num);
			LordCore.server.shutdown();
		}
		return null;
	}
	
	public Class<?>[] getParamTypes (Object... params) {
		Class<?>[] types = new Class<?>[params.length];
		int length = params.length;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				types[i] = params[i].getClass();
			}
		}
		return types;
	}
	
	public Object invoke (Class clazz, String method, Object... params) {
		return invoke(clazz, null, method, params);
	}
	
	public Object invoke (Object obj, String method, Object... params) {
		return invoke(obj.getClass(), obj, method, params);
	}
	
	// Закончить изменение онЕнабле, создать LordManager, заменить transient, кончить киты
	@SuppressWarnings("unchecked")
	public Object invoke (Class clazz, Object obj, String method, Object... params) {
		Object result = null;
		try {
			Method meth = clazz.getDeclaredMethod(method, getParamTypes(params));
			meth.setAccessible(true);
			result = meth.invoke(obj, params);
		}
		catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public <T> T newInstance (Class<T> clazz, Object... params) {
		T obj = null;
		try {
			obj = clazz.getConstructor(getParamTypes(params)).newInstance(params);
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
}
