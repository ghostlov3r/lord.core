package lord.core.game.group;

import lombok.Getter;
import lombok.var;
import lord.core.LordCore;
import lord.core.mgrbase.manager.LordManF;

import java.util.ArrayList;

/**
 * Менеджер групп
 */
public class GroupMan extends LordManF<Group, LordCore> {
	
	/** Группа по умолчанию */
	@Getter
	private Group defaultGroup;
	
	public GroupMan () {
		this.prettyJson();
		this.loadAll();
		
		if (getEntries().isEmpty()) {
			var group = createDefault();
			this.add(this.defaultGroup = group);
			group.save();
			getLogger().alert("Created default Group!");
		} else {
			forEach(this::addParentPermissions);
		}
		
		getLogger().info("Loaded " + getEntries().size() + " groups!");
	}
	
	/** Добавляет группе наследованные права */
	public void addParentPermissions (Group group) {
		if (!group.hasParent()) {
			return;
		}
		Group parent = this.getEntries().get(group.getParent());
		if (parent == null) {
			return;
		}
		this.addParentPermissions(parent);
		
		parent.getPermissions().forEach((perm) -> {
			if (!group.getPermissions().contains(perm)) {
				group.getPermissions().add(perm);
			}
		});
	}
	
	public Group createDefault () {
		var group = new Group();
		
		group.prefix = "none";
		group.clearPrefix = "none";
		group.isDefault = true;
		group.parent = "none";
		group.permissions = new ArrayList<>();
		group.permissions.add("example"); // remove
		group.permissions.add("example2"); // remove
		group.info = new GroupInfo(0, new ArrayList<>());
		
		group.finup("basic", this);
		return group;
	}
	
}
