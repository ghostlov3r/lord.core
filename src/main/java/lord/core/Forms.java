package lord.core;

import lord.core.util.form.SimpleFormV1;
import lord.core.gamer.Gamer;
import lord.core.util.file.AdvFolder;

import java.io.File;

public class Forms {
	
	public static final String NEXT_LINE = System.lineSeparator();
	public static final String DOUBLE_LINE = System.lineSeparator() + System.lineSeparator();
	
	public static void worldList (Gamer player) {
		AdvFolder folder = AdvFolder.get("worlds");
		folder.initFolder();
		File[] files = folder.getFile().listFiles();
		if (files == null) {
			player.sendMessage("Ошибка при получении списка файлов");
			return;
		}
		SimpleFormV1.SimpleFormBuilder form = SimpleFormV1.simple();
		for (File file : files) {
			String worldName = file.getName();
			form.button(worldName, (player1) -> {
				if (!LordCore.server.isLevelLoaded(worldName)) {
					if (!LordCore.server.loadLevel(worldName)) {
						player.sendMessage("Не удалось загрузить мир");
						return;
					}
				}
				player1.switchLevel(LordCore.server.getLevelByName(worldName));
			});
		}
		player.sendForm(form.build());
	}
	
}
