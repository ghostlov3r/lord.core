package lord.core.util;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@Getter @Accessors(fluent = true)
public class LordMap<Key, Value> implements Iterable<Value> {
	
	private Entry<Key, Value> first;
	
	private Entry<Key, Value> last;
	
	private int size = 0;
	
	public boolean isEmpty () {
		// new ObjectMapper().getFareadValue("", "".getClass())
		// new Gson().fromJson("", "")
		// new GsonBuilder().
		DeserializationConfig
		new ObjectMapper().set
		return size == 0;
	}
	
	public LordMap put (Key key, Value val) {
		Entry<Key, Value> entry = null;
		if (first != null) {
			Entry<Key, Value> wEntry = first;
			while (wEntry != null) {
				if (wEntry.key == key) {
					entry = wEntry;
					break;
				}
				wEntry = wEntry.next;
			}
		}
		
		if (entry == null) {
			entry = new Entry<>(key, val);
			if (first == null) {
				first = entry;
			} else {
				last.next = entry;
				entry.prev = last;
			}
			last = entry;
		} else {
			entry.value = val;
		}
		
		return this;
	}
	
	@Nullable
	public Value get (Key key) {
		if (first != null) {
			if (first.key == key) {
				return first.value;
			}
			Entry<Key, Value> next = first.next;
			while (next != null) {
				if (next.key == key) {
					return next.value;
				}
				next = next.next;
			}
		}
		return null;
	}
	
	public Value remove (Key key) {
		Entry<Key, Value> entry = first;
		while (entry != null) {
			if (entry.key == key) {
				if (entry.prev != null) {
					entry.prev.next = entry.next;
				}
				entry.next.prev = entry.prev; // fix
				if (first == entry) { // fix
					first = null;
				}
				if (last == entry) { // fix
					last = null;
				}
				return entry.value;
			}
			entry = entry.next;
		}
		return null;
	}
	
	@Getter
	private static class Entry<EKey, EValue> {
		final EKey key;
		EValue value;
		Entry<EKey, EValue> next;
		Entry<EKey, EValue> prev;
		
		private Entry (EKey key, EValue value) {
			this.key = key;
			this.value = value;
		}
	}
	
	@NotNull
	@Override
	public Iterator<Value> iterator () {
		return null;
	}
	
}
