package lord.core.game.group;

import dev.ghostlov3r.common.DiskMap;
import lombok.Getter;
import lord.core.LordCore;

import java.util.ArrayList;

import static dev.ghostlov3r.beengine.Server.logger;

/**
 * Менеджер групп
 */
public class GroupMan extends DiskMap<String, Group> {
	
	/** Группа по умолчанию */
	@Getter
	private Group defaultGroup;
	
	public GroupMan () {
		super(LordCore.instance().dataPath().resolve("groups"), Group.class);
		this.loadAll();

		if (isEmpty()) {
			var group = createDefault();
			this.put(group.key(), this.defaultGroup = group);
			group.save();
			logger().alert("Created default Group!");
		} else {
			values().forEach(this::addParentPermissions);
		}
		
		logger().info("Loaded " + size() + " groups!");
	}
	
	/** Добавляет группе наследованные права */
	public void addParentPermissions (Group group) {
		if (!group.hasParent()) {
			return;
		}
		Group parent = this.get(group.getParent());
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
		var group = new Group(this, "basic");
		
		group.prefix = "none";
		group.clearPrefix = "none";
		group.isDefault = true;
		group.parent = "none";
		group.permissions = new ArrayList<>();
		group.permissions.add("example"); // remove
		group.permissions.add("example2"); // remove
		group.info = new GroupInfo(0, new ArrayList<>());
		
		return group;
	}
	
}
