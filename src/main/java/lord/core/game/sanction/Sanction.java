package lord.core.game.sanction;

import lombok.Getter;
import lombok.var;
import lord.core.LordCore;
import lord.core.api.EventApi;
import lord.core.gamer.Gamer;
import lord.core.gamer.GamerData;
import lord.core.mgrbase.manager.LordManFA;

import static lord.core.game.sanction.BanEntry.daysToMillis;
import static lord.core.game.sanction.BanEntry.hoursToMillis;

/**
 * Управление наказаниями игроков
 */
@Getter
public class Sanction extends LordManFA<BanEntry, LordCore> {
	
	/** Причины бана */
	private final BanReasons reasons;
	
	/** Менеджер репортов */
	private final ReportManager reportManager;
	
	public Sanction () {
		reasons = new BanReasons(this);
		reportManager = new ReportManager(this);
		
		new BanCommand(this);
		
		EventApi.onPlayerPreLogin(event -> {
			var entry = get(event.getPlayer().getName());
			
			if (entry != null) {
				event.setKickMessage(entry.generateMessage());
				event.setCancelled();
			}
		});
	}
	
	/**
	 * @param name Имя игрока
	 * @return True, если игрок забанен
	 */
	public boolean isBanned (String name) {
		return this.get(name) != null;
	}
	
	/**
	 * Пробует получить из массива или загрузить
	 * с диска информацию о бане
	 * @param name Имя игрока
	 * @return Null, если вхождение не найдено и игрок не забанен
	 */
	@Override
	public BanEntry get (String name) {
		var entry = super.get(name);
		return isExpired(entry)
			? null
			: entry;
	}
	
	/**
	 * Удаляет вхождение бана, если время разбана наступило
	 * @param entry Информация о бане
	 * @return True, если время пришло и вхождение удалено
	 */
	private boolean isExpired (BanEntry entry) {
		if (entry.isForever()) {
			return false;
		}
		if (System.currentTimeMillis() > entry.getUntil()) {
			entry.deleteFile();
			remove(entry);
			return true;
		}
		return false;
	}
	
	/** Блокирует игрока */
	public BanEntry ban (String name, String blocker, String ruleID) {
		return ban(name, blocker, ruleID, 0);
	}
	
	/** Блокирует игрока */
	public BanEntry ban (String name, String blocker, String ruleID, int days) {
		return ban(name, blocker, ruleID, days, 0);
	}
	
	/** Блокирует игрока */
	public BanEntry ban (String name, String blocker, String ruleID, int days, int hours) {
		var entry = createEntry(name, blocker, ruleID, days, hours);
		this.add(entry);
		entry.save();
		getLogger().warning("Banned " + name + " until " + entry.getUnbanDate() + " (" + entry.getRuleID() + ")");
		return entry;
	}
	
	private BanEntry createEntry (String name, String blocker, String ruleID, int days, int hours) {
		var entry = BanEntry.builder()
							.blockerName(blocker)
							.ruleID(getReasons().get(ruleID) == null
								? reasons.getDefaultReason().getName()
								: ruleID)
							.until((hours == 0 && days == 0)
								? 0
								: System.currentTimeMillis() + hoursToMillis(hours) + daysToMillis(days))
							.build();
		entry.finup(name, this);
		return entry;
	}
	
	public void unban (String name) {
		var entry = this.get(name);
		if (entry != null) {
			entry.setUntil(System.currentTimeMillis() - 1000);
			entry.save();
		}
	}
	
	/** Запрещает чат */
	public void mute (Gamer gamer, String reason) {
		this.mute(gamer, reason, 0);
	}
	
	/** Запрещает чат */
	public void mute (Gamer gamer, String reason, int hours) {
		this.mute(gamer, reason, hours, 0);
	}
	
	/** Запрещает чат */
	public void mute (Gamer gamer, String reason, int hours, int minutes) {
		if (this.isMuted(gamer)) {
			return;
		}
		int mins = hours * 60 + minutes;
		if (mins == 0) {
			gamer.getData().setMutedUntil(GamerData.MUTED_FOREVER);
			LordCore.log.info("Muted " + gamer.getName() + " forever (" + reason + ")");
		} else {
			gamer.getData().setMutedUntil(System.currentTimeMillis() + mins * 60 * 1000);
			LordCore.log.info("Muted "  + gamer.getName() + " for " + mins + " minutes (" + reason + ")");
		}
	}
	
	/** Разрешает чат */
	public void unmute (Gamer gamer) {
		gamer.getData().setMutedUntil(GamerData.NOT_MUTED);
	}
	
	/** Имеет ли игрок запрет на чат */
	public boolean isMuted (Gamer gamer) {
		long value = gamer.getData().getMutedUntil();
		if (value == GamerData.NOT_MUTED) {
			return false;
		}
		if (value == GamerData.MUTED_FOREVER) {
			return true;
		}
		if (System.currentTimeMillis() > value) {
			this.unmute(gamer);
			return false;
		}
		return true;
	}
	
}
