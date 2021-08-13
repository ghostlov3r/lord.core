package lord.core.minigame.arena;

import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import lombok.var;
import lord.core.LordCore;
import lord.core.mgrbase.entry.LordEntryF;
import lord.core.mgrbase.manager.LordManF;
import lord.core.util.file.AdvFolder;

public class TeamColors extends LordManF<TeamColors.TeamColor, LordCore> {
	
	@Getter
	public static class TeamColor extends LordEntryF<TeamColors> {
		
		private String colorName;
		private int    woolMeta;
		
	}
	
	public TeamColors (AdvFolder folder) {
		super(folder);
		prettyJson();
		loadAll();
		loadDefaults();
	}
	
	private void defColor (String name, TextFormat format, DyeColor wool) {
		var color = get(name);
		if (color == null) {
			color = new TeamColor();
			color.colorName = format.name();
			color.woolMeta = wool.getWoolData();
			color.finup(name, this);
			color.save();
			this.add(color);
		}
	}
	
	private void loadDefaults () {
		
		defColor("blue",   TextFormat.DARK_BLUE,   DyeColor.BLUE   );
		defColor("red",    TextFormat.DARK_RED,    DyeColor.RED    );
		defColor("yellow", TextFormat.YELLOW,      DyeColor.YELLOW );
		defColor("green",  TextFormat.GREEN,       DyeColor.GREEN  );
		
		defColor("purple", TextFormat.DARK_PURPLE, DyeColor.PURPLE );
		defColor("orange", TextFormat.GOLD,        DyeColor.ORANGE );
		defColor("cyan",   TextFormat.AQUA,        DyeColor.CYAN   );
		defColor("black",  TextFormat.BLACK,       DyeColor.BLACK  );
		
	}
	
	public TeamColor getByColorChar (String colorChar) {
		return getByColorChar(colorChar.charAt(0));
	}
	
	public TeamColor getByColorChar (char color) {
		switch (color) {
			case '1': return get("blue");
			case '4': return get("red");
			case 'e': return get("yellow");
			case 'a': return get("green");
			
			case '5': return get("purple");
			case '6': return get("orange");
			case 'b': return get("cyan");
			case '0': return get("black");
		}
		return null;
	}
	
}
