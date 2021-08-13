package lord.core.util.unused;

import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class TTest {
	
	static int times = 0;
	static int buff = 0;
	
	public static void mainn(String args[]) {
		// System.out.println("Sum of x+y = " + z);
		
		String[] strings = {
			"PlayerJoinEvent",
			"PlayerQuitEvent",
			"PlayerInteractEvent",
			"PlayerChatEvent",
			"PlayerLoginEvent",
			"PlayerToggleSwimEvent",
			"PlayerToggleSprintEvent",
			"PlayerTeleportEvent",
			"PlayerDropItemEvent",
			"PlayerCreationEvent",
			"EntityDamageByEntityEvent",
			"EntityDamageEvent",
			"PlayerJoinEvent",
			
			"PlayerJoinEvent1",
			"PlayerQuitEvent1",
			"PlayerInteractEvent1",
			"PlayerChatEvent1",
			"PlayerLoginEvent1",
			"PlayerToggleSwimEvent1",
			"PlayerToggleSprintEvent1",
			"PlayerTeleportEvent1",
			"PlayerDropItemEvent1",
			"PlayerCreationEvent1",
			"EntityDamageByEntityEvent1",
			"EntityDamageEvent1",
			"PlayerJoinEvent1",
			
			
			"PlayerJoinEvent2",
			"PlayerQuitEvent2",
			"PlayerInteractEvent2",
			"PlayerChatEvent2",
			"PlayerLoginEvent2",
			"PlayerToggleSwimEvent2",
			"PlayerToggleSprintEvent2",
			"PlayerTeleportEvent2",
			"PlayerDropItemEvent2",
			"PlayerCreationEvent2",
			"EntityDamageByEntityEvent2",
			"EntityDamageEvent2",
			"PlayerJoinEvent2",
			
			"PlayerJoinEvent3",
			"PlayerQuitEvent3",
			"PlayerInteractEvent3",
			"PlayerChatEvent3",
			"PlayerLoginEvent3",
			"PlayerToggleSwimEvent3",
			"PlayerToggleSprintEvent3",
			"PlayerTeleportEvent3",
			"PlayerDropItemEvent3",
			"PlayerCreationEvent3",
			"EntityDamageByEntityEvent3",
			"EntityDamageEvent3",
			"PlayerJoinEvent3",
			
			"PlayerJoinEvent4",
			"PlayerQuitEvent4",
			"PlayerInteractEvent4",
			"PlayerChatEvent4",
			"PlayerLoginEvent4",
			"PlayerToggleSwimEvent4",
			"PlayerToggleSprintEvent4",
			"PlayerTeleportEvent4",
			"PlayerDropItemEvent4",
			"PlayerCreationEvent4",
			"EntityDamageByEntityEvent4",
			"EntityDamageEvent4",
			"PlayerJoinEvent4",
			
			"PlayerJoinEvent5",
			"PlayerQuitEvent5",
			"PlayerInteractEvent5",
			"PlayerChatEvent5",
			"PlayerLoginEvent5",
			"PlayerToggleSwimEvent5",
			"PlayerToggleSprintEvent5",
			"PlayerTeleportEvent5",
			"PlayerDropItemEvent5",
			"PlayerCreationEvent5",
			"EntityDamageByEntityEvent5",
			"EntityDamageEvent5",
			"PlayerJoinEvent5",
			
			
		};
		
		String g;
		
		long start = System.currentTimeMillis();
		for (String str : strings) {
			g = str.hashCode() + " :: " + (15 & hash(str));
		}
		System.out.println(System.currentTimeMillis() - start);
		System.out.println("");
		
		
		long start1 = System.currentTimeMillis();
		for (String str : strings) {
			g = str.hashCode() + " :: " + (15 & hash(str));
		}
		System.out.println(System.currentTimeMillis() - start1);
		System.out.println("");
		
		
		long start2 = System.currentTimeMillis();
		for (String str : strings) {
			g = str.hashCode() + " :: " + (15 & hash(str));
		}
		System.out.println(System.currentTimeMillis() - start2);
		System.out.println("");
		
		
		HashMap<String, Integer> map = new HashMap<>();
		for (String str : strings) {
			for (int i = 1; i < 10000; i++)
				map.put(str + i, 5 + i);
		}
		
		
		System.out.println("LAMBDA");
		long start3 = System.currentTimeMillis();
		map.forEach((key, val) -> map.get(key));
		System.out.println(System.currentTimeMillis() - start3);
		System.out.println("");
		
		
		System.out.println("LAMBDA");
		long start4 = System.currentTimeMillis();
		map.forEach((key, val) -> map.get(key));
		System.out.println(System.currentTimeMillis() - start4);
		System.out.println("");
		
		
		System.out.println("METH");
		long start5 = System.currentTimeMillis();
		map.forEach((key, val) -> {
			try {
				Method meth = map.getClass().getDeclaredMethod("get", Object.class);
				meth.setAccessible(true);
				// meth.invoke(obj, params);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		System.out.println(System.currentTimeMillis() - start5);
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("");
		
		
		// int times = 0;
		System.out.println("METH + INVOKE");
		long start6 = System.currentTimeMillis();
		map.forEach((key, val) -> {
			try {
				Method meth = map.getClass().getDeclaredMethod("get", Object.class);
				meth.setAccessible(true);
				Field f = TTest.class.getDeclaredField("buff");
				f.set(null, meth.invoke(map, key));
				f.set(null, meth.invoke(map, key));
				// meth.invoke(map, key);//kek
				// System.out.println(meth.invoke(map, key));
				times++;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		System.out.println(System.currentTimeMillis() - start6 + (" " + times ));
		System.out.println(buff);
      
      
      /*for (int i = 1; i < 10000; i++) {
      
      }*/
		
	}
	
	public static int hash(Object key) {
		int h;
		return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}
}