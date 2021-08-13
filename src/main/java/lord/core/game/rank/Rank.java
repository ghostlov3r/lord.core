package lord.core.game.rank;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lord.core.mgrbase.entry.LordEntryF;
import org.jetbrains.annotations.Nullable;

@Getter @Builder @NoArgsConstructor
public class Rank extends LordEntryF<RankMan> {
	
	/** Имя предыдущего ранга   */  @Nullable private String     prevName;
	/** Имя следующего ранга    */  @Nullable private String     nextName;
	/** Выдается новым игрокам  */            private boolean    isDefault;
	/** Макс. опыт ранга        */            private int        maxExp;
	/** Размер денежной награды */            private int        moneyReward;
	/** Имя или описание ранга  */  @Nullable private String     rankText;
	/** Сообщение при получении */  @Nullable private String     onGetMessage;
	/** Список прав             */  @Nullable protected String[] permissions;
	
	
	public boolean hasPrev () {
		return !(null == this.prevName || "".equals(this.prevName));
	}
	
	public boolean hasNext () {
		return !(null == this.nextName || "".equals(this.nextName));
	}
	
	public Rank getPrev () {
		return this.getManager().get(this.prevName);
	}
	
	public Rank getNext () {
		return this.getManager().get(this.nextName);
	}
	
}
