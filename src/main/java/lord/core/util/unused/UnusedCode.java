package lord.core.util.unused;

public class UnusedCode {
	
	/*
	Config config = Folder.MAIN.createConfig("groups.yml");
	if (config.getRootSection().isEmpty()) {
			ConfigSection basic = new ConfigSection();
			basic.set("prefix", "none");
			basic.set("default", true);
			basic.set("parent", "none");
			basic.set("permissions", new ArrayList<String>());
			config.set("basic", basic);
		}
		config.save();
		 */
	
	/* public AvailableCommandsPacket createPacketFor(Player gamer) {
		AvailableCommandsPacket pk = new AvailableCommandsPacket();
		List<CommandData> data = pk.getCommands();
		for (Command addCommand : this.registeredCommands.values()) {
			if (!addCommand.testPermissionSilent(gamer)) {
				continue;
			}
			data.addRegion(addCommand.toNetwork(gamer));
		}
		return pk;
	} */
	
	/* private static void createMainFolders()
	{
		new ArrayList<>(Arrays.asList(Folder.class.getFields())).forEach((field -> {
			try {
				AdvFolder folder = (AdvFolder) field.get(null);
				if (!folder.mkdirIfNot()) {
					LordCore.log.error("Error happened during creating Folder " + folder.getPath().toUpperCase());
				}
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}));
	} */
	
	/* Конвертирует LordRunnable в Nukkit Task */
	/* private Task convertToTask(LordRunnable task) {
		return new Task() {
			@Override
			public void onRun (int i) {
				task.onRun();
			}
		};
	}*/
	
}
