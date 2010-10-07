package bench.idea_01;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("serial")
class RegKeyListMap extends ConcurrentHashMap<RegKey, RegKeyList> {

	RegKeyList obtain(final RegKey key) {

		RegKeyList list = get(key);

		if (list == null) {
			list = new RegKeyList();
			putIfAbsent(key, list);
			list = get(key);
			assert list != null;
		}

		return list;

	}

}
