package lord.core.game.group;

import beengine.util.DiskMap;
import lord.core.Lord;

import java.util.ArrayList;

import static beengine.Server.logger;

/**
 * Менеджер групп
 */
public class GroupMan extends DiskMap<String, Group> {
	
	/** Группа по умолчанию */
	private Group defaultGroup;
	
	public GroupMan () {
		super(Lord.instance.dataPath().resolve("groups"), Group.class);
		loadAll();
		defaultGroup = values().stream().filter(Group::isDefault).findAny().orElseGet(() -> {
			Group group = createDefault();
			add(group);
			group.save();
			logger().alert("Created default Group!");
			return group;
		});
		values().forEach(this::addParentPermissions);
		logger().info("Loaded " + size() + " groups!");
	}

	public Group defaultGroup() {
		return defaultGroup;
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
		addParentPermissions(parent);

		group.permissions.addAll(parent.permissions);
	}
	
	public Group createDefault () {
		var group = new Group(this, "basic");
		
		group.prefix = "none";
		group.clearPrefix = "none";
		group.isDefault = true;
		group.parent = "none";
		group.permissions.add("example"); // remove
		group.permissions.add("example2"); // remove
		group.info = new GroupInfo(0, new ArrayList<>());
		
		return group;
	}
	
}
