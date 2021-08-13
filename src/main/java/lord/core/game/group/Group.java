package lord.core.game.group;

import lombok.Getter;
import lord.core.gamer.Gamer;
import lord.core.mgrbase.entry.LordEntryF;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
public class Group extends LordEntryF<GroupMan> {
	
	/** Префикс               */  protected String prefix;
	/** Префикс без цветов    */  protected String clearPrefix;
	/** Имя группы-родителя   */  @Nullable protected String parent;
	/** Является ли дефолтной */  protected boolean isDefault;
	/** Права                 */  protected List<String> permissions;
	/** Прочая информация     */  protected GroupInfo info;
	
	/**
	 * @return True, если есть родитель
	 */
	public boolean hasParent () {
		return parent != null;
	}
	
	public void giveTo (Gamer gamer) {
		Group group = gamer.getGroup();
		if (group != null) {
			if (group == this) {
				return;
			}
			gamer.removeNukkitPermissions(group.getPermissions());
		}
		gamer.getData().setGroupName(this.name);
		gamer.gro
	}
	
}
