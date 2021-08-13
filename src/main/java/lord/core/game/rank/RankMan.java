package lord.core.game.rank;

import lombok.Getter;
import lombok.var;
import lord.core.LordCore;
import lord.core.mgrbase.manager.LordManF;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Менеджер рангов
 */
@Getter
public class RankMan extends LordManF<Rank, LordCore> {
	
	public boolean usingForms;
	
	private Rank defaultRank;
	
	private Rank lastRank;
	
	public RankMan () {
		prettyJson();
		usingForms = false;
		loadAll();
		
		forEach(rank -> {
			if (rank.isDefault()) defaultRank = rank;
		});
		if (defaultRank == null) {
			var def = createDefault();
			if (getEntries().isEmpty()) def.save();
			this.add(def);
		}
		recalculatePermissions();
		logLoadedEntries();
	}
	
	/** Добавляет рангам права предыдущих */
	private void recalculatePermissions () {
		var rank = this.getDefaultRank();
		while (rank != null) {
			var perms = new ArrayList<String>();
			if (rank.getPermissions() != null) {
				perms.addAll(Arrays.asList(rank.getPermissions()));
			}
			var next = rank.getNext();
			if (next != null && next.getPermissions() != null) {
				perms.addAll(Arrays.asList(next.getPermissions()));
			}
			rank.permissions = perms.toArray(new String[0]);
			rank = next;
		}
	}
	
	/** Создает пример ранга по умолчанию */
	public Rank createDefault () {
		var rank = Rank.builder()
					   .prevName(null)     .nextName(null)
					   .isDefault(true)    .maxExp(1000)
					   .moneyReward(0)     .rankText(null)
					   .onGetMessage(null) .permissions(new String[] { "example", "example" })
					   .build();
		rank.finup("0", this);
		return rank;
	}
	
}
