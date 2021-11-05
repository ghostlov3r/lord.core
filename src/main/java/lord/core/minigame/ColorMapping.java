package lord.core.minigame;

import dev.ghostlov3r.beengine.block.utils.DyeColor;
import dev.ghostlov3r.beengine.utils.TextFormat;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ColorMapping {

	public TextFormat formatByDye (DyeColor color) {
		return switch (color) {
			case ORANGE -> TextFormat.GOLD;
			case YELLOW -> TextFormat.YELLOW;
			case GRAY -> TextFormat.DARK_GRAY;
			case LIGHT_GRAY -> TextFormat.GRAY;
			case CYAN -> TextFormat.AQUA;
			case PURPLE, MAGENTA -> TextFormat.LIGHT_PURPLE;
			case BLUE, LIGHT_BLUE -> TextFormat.BLUE;
			case GREEN, LIME -> TextFormat.GREEN;
			case RED, PINK -> TextFormat.RED;
			case BLACK -> TextFormat.BLACK;

			default -> TextFormat.WHITE;
		};
	}
}
