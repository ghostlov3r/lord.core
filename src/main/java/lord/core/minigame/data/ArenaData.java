package lord.core.minigame.data;

import dev.ghostlov3r.common.DiskEntry;
import dev.ghostlov3r.common.DiskMap;
import dev.ghostlov3r.math.Vector3;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
@Getter
public class ArenaData extends DiskEntry<Integer> {

	public String type;
	List<Vector3> statePos = new ArrayList<>();

	public ArenaData(DiskMap<Integer, ?> map, Integer key) {
		super(map, key);
	}
}
