package lord.core.union;

import dev.ghostlov3r.beengine.utils.config.Config;
import dev.ghostlov3r.beengine.utils.config.Name;

import java.util.ArrayList;
import java.util.List;

@Name("union")
public class UnionConfig extends Config {

	public int statusSendFrequency = 10;
	public List<Entry> servers = new ArrayList<>();

	public static class Entry {
		int id;
		public String name;
		public String ip;
		public int port;
	}
}
