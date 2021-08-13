package lord.core.gamer;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lord.core.game.auth.RegisterData;
import lord.core.util.json.JsonSkip;
import lord.core.mgrbase.entry.LordEntryF;

@Getter
public abstract class GamerData<GDM extends GamerMan, G extends Gamer>
	extends LordEntryF<GDM> {
	
	/** Владалец данных  */          @JsonSkip protected G gamer;
	
	/** Группа           */  @Setter @SerializedName("g") protected String groupName;
	/** Баланс           */  @Setter @SerializedName("m") protected int    money;
	/** Ранк (Уровень)   */          @SerializedName("r") protected String rank;
	/** Опыт ранка       */          @SerializedName("e") protected int    rankExp;
	/** Время игры мин   */  @Setter @SerializedName("t") protected int    playedMinutes; // todo
	/** Время посл. auth */          @SerializedName("a") protected long   lastAuth;
	/** Посл. IP auth    */          @SerializedName("i") protected String lastAuthIP;
	/** Пароль           */          @SerializedName("p") protected String password;
	
	/** Почта            */  @Setter @SerializedName("l") protected String   email;
	/** Ссылка вк        */  @Setter @SerializedName("v") protected String   vklink;
	
	/** Кол-во мутов     */  @Setter @SerializedName("c") protected int      muteCounter;
	/** Время анмута     */  @Setter @SerializedName("u") protected long     mutedUntil;
	
	public static final long NOT_MUTED = -1;
	public static final long MUTED_FOREVER = 0;
	
	public void setRegData (RegisterData data) {
		this.password = data.password;
		this.email = data.email;
		this.vklink = data.vklink;
	}
	
	public void addMoney (int amount) {
		this.money += amount;
	}
	
	public void removeMoney (int amount) {
		this.money -= amount;
	}
	
}
