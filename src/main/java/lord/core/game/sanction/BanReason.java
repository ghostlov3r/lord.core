package lord.core.game.sanction;

import lombok.Getter;
import lord.core.mgrbase.entry.LordEntryF;

/**
 * Причина для бана игрока
 * @author ghostlov3r
 */
@Getter
public class BanReason extends LordEntryF<BanReasons> {
	
	/** Краткий текст причины бана */
	protected String text;
	
	/**
	 * @return Возвращает ID правила
	 */
	@Override
	public String getName () {
		return super.getName();
	}
}
