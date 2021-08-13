package lord.core.rework;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import overlord.Main;

public class World {
	
	// Лобби мир
	public static Level lobby;
	public static final String LOBBY_NAME = "anar";
	
	// Игровой мир
	public static Level main;
	public static final String MAIN_NAME = "world";
	
	public static Server server;
	
	public static void init()
	{
		server = Main.server;
		lobby = server.getLevelByName(LOBBY_NAME);
		// loadMain();
	}
	
	/*private static void loadMain()
	{
		if (!server.isLevelLoaded(MAIN_NAME)) server.loadLevel(MAIN_NAME);
		if (!server.isLevelLoaded(LOBBY_NAME)) {
			server.loadLevel(LOBBY_NAME); //убрать
			lobby = server.getLevelByName(LOBBY_NAME);
		}
		main = server.getLevelByName(MAIN_NAME);
	}*/
	
}
