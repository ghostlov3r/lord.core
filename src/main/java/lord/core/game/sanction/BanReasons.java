package lord.core.game.sanction;

import lombok.Getter;
import lombok.val;
import lombok.var;
import lord.core.LordCore;
import lord.core.mgrbase.manager.LordManF;

/**
 * Причины для бана игрока
 */
@Getter
public class BanReasons extends LordManF<BanReason, LordCore> {
	
	/** Причина бана по умолчанию */
	private BanReason defaultReason;
	
	public BanReasons (Sanction sanction) {
		super(sanction.getFolder().getChild("reasons"));
		prettyJson();
		loadAll();
		
		val defaultReason = createDefault();
		
		forEach(reason -> {
			if (reason.getName().equals(defaultReason.getName())) {
				this.defaultReason = reason;
			}
		});
		
		if (this.defaultReason == null) {
			this.defaultReason = defaultReason;
			this.add(defaultReason);
			defaultReason.save();
			getLogger().info("Loaded default reason");
		}
	}
	
	/**
	 * Возвращает причину бана по ID правила
	 * @return Вернет причину по умолчанию, если такой нет
	 */
	public BanReason getOrDefault (String ruleID) {
		var reason = this.get(ruleID);
		return reason == null
			? this.defaultReason
			: reason;
	}
	
	/**
	 * Создает причину бана по умолчанию
	 */
	public BanReason createDefault () {
		val reason = new BanReason();
		reason.text = "Неизвестная причина";
		reason.finup("0", this);
		return reason;
	}
	
}
