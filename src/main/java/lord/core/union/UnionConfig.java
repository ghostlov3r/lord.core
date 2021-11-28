package lord.core.union;

import dev.ghostlov3r.beengine.utils.config.Config;
import dev.ghostlov3r.beengine.utils.config.Name;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Name("union")
public class UnionConfig extends Config {

	public int statusSendFrequency = 10;
	public Map<String, Entry> servers = new HashMap<>();

	public static class Entry {
		public String name;
		public String ip;
		public int port;
	}
}
