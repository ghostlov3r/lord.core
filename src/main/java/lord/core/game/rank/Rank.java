package lord.core.game.rank;

import beengine.util.DiskEntry;
import beengine.util.DiskMap;
import fastutil.set.impl.ObjectHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class Rank extends DiskEntry<Integer> {

	/** Имя предыдущего ранга   */  @Nullable private Integer     prevIdx;
	/** Имя следующего ранга    */  @Nullable private Integer     nextIdx;
	/** Выдается новым игрокам  */            private boolean    isDefault;
	/** Макс. опыт ранга        */            private int        maxExp;
	/** Размер денежной награды */            private int        moneyReward;
	/** Размер денежной награды */            private int        goldReward;
	/** Имя или описание ранга  */  @Nullable private String     rankText;
	/** Сообщение при получении */  @Nullable private String     onGetMessage;
	/** Список прав             */  		  protected Set<String> permissions = new ObjectHashSet<>();

	public Rank(DiskMap<Integer, ?> map, Integer key) {
		super(map, key);
	}

	public boolean hasPrev () {
		return prevIdx != null;
	}
	
	public boolean hasNext () {
		return nextIdx != null;
	}
	
	public Rank getPrev () {
		return (Rank) this.map().get(prevIdx);
	}
	
	public Rank getNext () {
		return (Rank) this.map().get(nextIdx);
	}

}
