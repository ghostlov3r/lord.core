package lord.core.game.rank;

import beengine.util.DiskMap;
import fastutil.set.impl.RefHashSet;
import lombok.Getter;
import lombok.experimental.Accessors;
import lord.core.Lord;

import java.util.List;

/**
 * Менеджер рангов
 */
@Accessors(fluent = true)
@Getter
public class RankMan extends DiskMap<Integer, Rank> {
	
	public boolean usingForms;
	
	private Rank defaultRank;

	public RankMan () {
		super(Lord.instance.dataPath().resolve("ranks"), Rank.class, Integer.class);
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
		values().forEach(rank -> {
			if (!rank.hasPrev()) {
				Rank next;
				while (rank.hasNext()) {
					next = rank.getNext();
					next.permissions.addAll(rank.permissions);
					rank = next;
				}
			}
		});
	}
	
	/** Создает пример ранга по умолчанию */
	public Rank createDefault () {
		return new Rank(this, 0)
					   .prevIdx(null)     .nextIdx(null)
					   .isDefault(true)    .maxExp(1000)
					   .moneyReward(0)     .rankText(null)
					   .onGetMessage(null) .permissions(new RefHashSet<>(List.of("example", "example")));
	}
	
}
