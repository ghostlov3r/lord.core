package lord.core.game.kit;

import lombok.Getter;
import lombok.Setter;
import lombok.var;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Информация о предмете из набора
 */
@Getter
public class KitItemData {
	
	/** ID предмета */
	private int id;
	
	/** Meta предмета */
	private int meta;
	
	/** Количество */
	@Setter private int amount;
	
	/** Кастомное имя */
	@Setter @Nullable private String name;
	
	/** Список зачарований */
	@Nullable private List<KitItemEnchData> enchs;
	
	public void addEnch (int id, int level) {
		this.addEnch(KitItemEnchData.create(id, level));
	}
	
	public void addEnch (KitItemEnchData data) {
		if (this.enchs == null) {
			this.enchs = new ArrayList<>();
		}
		this.enchs.add(data);
	}
	
	public static KitItemData create (int id) {
		return create(id, 0);
	}
	
	public static KitItemData create (int id, int meta) {
		return create(id, meta, 1);
	}
	
	public static KitItemData create (int id, int meta, int amount) {
		var data = new KitItemData();
		data.id = id;
		data.meta = meta;
		data.amount = amount;
		return data;
	}
	
}
