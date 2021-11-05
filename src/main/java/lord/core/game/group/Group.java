package lord.core.game.group;

import dev.ghostlov3r.common.DiskEntry;
import dev.ghostlov3r.common.DiskMap;
import lombok.Getter;
import lord.core.gamer.Gamer;
import javax.annotation.Nullable;
import java.util.List;

@Getter
public class Group extends DiskEntry<String> {
	
	/** Префикс               */  protected String prefix;
	/** Префикс без цветов    */  protected String clearPrefix;
	/** Имя группы-родителя   */  @Nullable
	protected String parent;
	/** Является ли дефолтной */  protected boolean isDefault;
	/** Права                 */  protected List<String> permissions;
	/** Прочая информация     */  protected GroupInfo info;

	public Group(DiskMap<String, ?> map, String key) {
		super(map, key);
	}

	/**
	 * @return True, если есть родитель
	 */
	public boolean hasParent () {
		return parent != null;
	}
	
	public void giveTo (Gamer gamer) {
		Group group = gamer.group();
		if (group != null) {
			if (group == this) {
				return;
			}
			// gamer.removeNukkitPermissions(group.getPermissions());
		}
		gamer.setGroup(group);
	}
	
}
