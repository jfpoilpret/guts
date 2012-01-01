package bench.idea_02.provider;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

final class RegMethodEntriesMap {

	private final ConcurrentMap<Method, RegEntryList> map = new MapMaker()
			.makeMap();

	void store(Method method, RegEntry entry) {

		final RegEntryList list = obtain(method);

		list.store(entry);

	}

	RegEntryList obtain(final Method method) {

		RegEntryList list = map.get(method);

		if (list == null) {
			list = new RegEntryList();
			map.putIfAbsent(method, list);
			list = map.get(method);
			assert list != null;
		}

		return list;

	}

}
