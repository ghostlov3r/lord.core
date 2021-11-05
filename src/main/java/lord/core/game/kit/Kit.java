package lord.core.game.kit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.ghostlov3r.common.DiskEntry;
import dev.ghostlov3r.common.DiskMap;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Kit extends DiskEntry<String> {
	
	/** Право на использование, пустое - право не нужно */
	@Nullable
	protected String permission;
	
	/** Информация о предметах */
	@Nullable protected ArrayList<KitItemData> items;
	
	/** Шлем */
	@Nullable protected KitItemData helmet;
	
	/** Доспех */
	@Nullable protected KitItemData chestplate;
	
	/** Штаны */
	@Nullable protected KitItemData leggins;
	
	/** Ботинки */
	@Nullable protected KitItemData boots;
	
	/** Выдаваемые эффекты */
	@Nullable protected ArrayList<KitEffectData> effects;
	
	/** Сообщение при выдаче */
	@Nullable protected String message;
	
	/** Часы кулдауна */
	protected int hours;
	
	/** Минуты кулдауна */
	protected int minutes;
	
	/** Кулдаун в миллисекунлах */
	@JsonIgnore
	private long cooldown;
	
	/** Имя игрока -> Следующее использование */
	protected Map<String, Long> times;

	public Kit(DiskMap<String, ?> map, String key) {
		super(map, key);

		this.cooldown = (this.hours * 60 * 60 * 1000) + (this.minutes * 60 * 1000);
		if (((KitMan)map).getConfig().isSaveTimes()) {
			deleteExpiredEntries();
		} else {
			times = new HashMap<>();
		}
	}
	
	/**
	 * Время следующего использования набора
	 * @return Если кулдауна нет, вернет 0
	 * @param name Имя игрока
	 */
	public long getNextTime (String name) {
		Long time = this.times.get(name);
		if (time == null) {
			return 0;
		}
		return time;
	}
	
	/** Удаляет из мапы истекшие кулдауны */
	public void deleteExpiredEntries () {
		var expiredNames = new ArrayList<String>();
		
		times.forEach((playerName, nextUse) -> {
			if (System.currentTimeMillis() > nextUse) {
				expiredNames.add(playerName);
			}
		});
		
		expiredNames.forEach(playerName -> {
			times.remove(playerName);
		});
	}
	
}
