package lord.core.util.unused;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenTest {
	public static void mainn(String args[]) {
		System.out.println();
		GenTest my = new GenTest();
	}
	
	public GenTest () {
		new TestMgr();
	}
	
	private class TestGamer extends Gamer<TestData, TestMgr> {
		public TestGamer () {
			super();
		}
	}
	
	private class TestData extends GamerData<TestMgr, TestGamer> {
	
	}
	
	private class TestMgr extends GamerDataMan<TestData, LordCore, TestGamer> {
		public TestMgr () {
			super();
		}
	}
	
	/** ----------------------------------------------------------------------------- */
	
	public abstract class GamerDataMan<GD extends GamerData, P extends LordPlugin, G extends Gamer> extends LordManF<GD, P> {
	
	}
	
	public abstract class GamerData<GDM extends GamerDataMan, G extends Gamer> extends LordEntryF<GDM> {
	
	}
	
	public abstract class Gamer<GD extends GamerData, GDM extends GamerDataMan> {
	
	}
	
	/** ----------------------------------------------------------------------------- */
	
	public abstract class LordMan<Entry extends LordEntry, Plug extends LordPlugin> {
		public LordMan () {
			System.out.println(getSuperGenericClasses(this)[0].getSimpleName());
			System.out.println(getSuperGenericClasses(this)[1].getSimpleName());
		}
	}
	
	public abstract class LordManF<EntryF extends LordEntryF, Plug extends LordPlugin> extends LordMan<EntryF, Plug> {
	
	}
	
	public abstract class LordEntry<Mgr extends LordMan> {
	
	}
	
	public abstract class LordEntryF<MgrF extends LordManF> extends LordEntry<MgrF> {
	
	}
	
	public abstract class LordPlugin {
	
	}
	
	public class LordCore extends LordPlugin {
	
	}
	
	public static Type[] getSuperGenericTypes (Object obj) {
		return getSuperGenericTypes(obj.getClass());
	}
	
	public static Type[] getSuperGenericTypes (Class<?> clazz) {
		return ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
	}
	
	public static String[] getSuperGenericNames (Object obj) {
		return getSuperGenericNames(obj.getClass());
	}
	
	public static String[] getSuperGenericNames (Class<?> clazz) {
		Type[] types = getSuperGenericTypes(clazz);
		String[] names = new String[types.length];
		
		if (types.length > 0) {
			for (int i = 0; i < types.length; i++) {
				names[i] = types[i].getTypeName();
			}
		}
		return names;
	}
	
	public static Class<?>[] getSuperGenericClasses (Object obj) {
		return getSuperGenericClasses(obj.getClass());
	}
	
	
	public static Class<?>[] getSuperGenericClasses (Class<?> clazz) {
		String[] names = getSuperGenericNames(clazz);
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
}