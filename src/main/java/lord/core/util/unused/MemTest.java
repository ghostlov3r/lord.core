package lord.core.util.unused;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class MemTest {
	
	public static ArrayList<HashMap<String, String>> lists = new ArrayList<>();
	static Runtime runtime = Runtime.getRuntime();
	
	public static void maiin(String args[]) {
		
		
		long start5 = System.currentTimeMillis();
		for (int i = 1; i < 100000; i++) {
			try {
				Method meth = MyClass.class.getDeclaredMethod("kek");
				meth.setAccessible(true);
				meth.invoke(null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(System.currentTimeMillis() - start5);
		mem();
		System.out.println("");
		
		long start6 = System.currentTimeMillis();
		for (int i = 1; i < 100000; i++) {
			try {
				Method meth = MyClass.class.getDeclaredMethod("kek");
				meth.setAccessible(true);
				meth.invoke(null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(System.currentTimeMillis() - start6);
		mem();
		System.out.println("");
		
		long start7 = System.currentTimeMillis();
		for (int i = 1; i < 100000; i++) {
			try {
				Method meth = MyClass.class.getDeclaredMethod("kek");
				meth.setAccessible(true);
				meth.invoke(null);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(System.currentTimeMillis() - start7);
		mem();
		mem();
		System.out.println("");
		
		long start8 = System.currentTimeMillis();
		for (int i = 1; i < 1000000; i++) {
			lists.add(new HashMap());
		}
		System.out.println(System.currentTimeMillis() - start8);
		mem();
		
	}
	
	public static void kek () {
	
	}
	
	public static void mem () {
		System.out.println((double) (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
	}
}