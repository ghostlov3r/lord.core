package lord.core.game.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @AllArgsConstructor @NoArgsConstructor
public class GroupInfo {
	
	/** Цена покупки этой группы */
	private int price;
	
	/** Информационный пункты (для отображения в форме с информацией) */
	private List<String> lines;
	
}
