package lord.core.game.rank;

import dev.ghostlov3r.beengine.utils.DiskEntry;
import dev.ghostlov3r.beengine.utils.DiskMap;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import javax.annotation.Nullable;

@Accessors(fluent = true, chain = true)
@Getter
@Setter
public class Rank extends DiskEntry<Integer> {

	/** Имя предыдущего ранга   */  @Nullable private String     prevName;
	/** Имя следующего ранга    */  @Nullable private String     nextName;
	/** Выдается новым игрокам  */            private boolean    isDefault;
	/** Макс. опыт ранга        */            private int        maxExp;
	/** Размер денежной награды */            private int        moneyReward;
	/** Имя или описание ранга  */  @Nullable private String     rankText;
	/** Сообщение при получении */  @Nullable private String     onGetMessage;
	/** Список прав             */  @Nullable protected String[] permissions;

	public Rank(DiskMap<Integer, ?> map, Integer key) {
		super(map, key);
	}


	public boolean hasPrev () {
		return !(null == this.prevName || "".equals(this.prevName));
	}
	
	public boolean hasNext () {
		return !(null == this.nextName || "".equals(this.nextName));
	}
	
	public Rank getPrev () {
		return (Rank) this.map().get(this.prevName);
	}
	
	public Rank getNext () {
		return (Rank) this.map().get(this.nextName);
	}
	
}
