package lord.core.game.rank;

import dev.ghostlov3r.common.DiskMap;
import lombok.Getter;
import lord.core.LordCore;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Менеджер рангов
 */
@Getter
public class RankMan extends DiskMap<Integer, Rank> {
	
	public boolean usingForms;
	
	private Rank defaultRank;
	
	private Rank lastRank;
	
	public RankMan () {
		super(LordCore.instance().dataPath().resolve("ranks"), Rank.class);
		usingForms = false;
		loadAll();
		
		values().forEach(rank -> {
			if (rank.isDefault()) defaultRank = rank;
		});
		if (defaultRank == null) {
			var def = createDefault();
			if (values().isEmpty()) def.save();
			this.put(def.key(), def);
		}
		recalculatePermissions();
	}
	
	/** Добавляет рангам права предыдущих */
	private void recalculatePermissions () {
		var rank = this.getDefaultRank();
		while (rank != null) {
			var perms = new ArrayList<String>();
			if (rank.permissions() != null) {
				perms.addAll(Arrays.asList(rank.permissions()));
			}
			var next = rank.getNext();
			if (next != null && next.permissions() != null) {
				perms.addAll(Arrays.asList(next.permissions()));
			}
			rank.permissions = perms.toArray(new String[0]);
			rank = next;
		}
	}
	
	/** Создает пример ранга по умолчанию */
	public Rank createDefault () {
		return new Rank(this, 0)
					   .prevName(null)     .nextName(null)
					   .isDefault(true)    .maxExp(1000)
					   .moneyReward(0)     .rankText(null)
					   .onGetMessage(null) .permissions(new String[] { "example", "example" });
	}
	
}
