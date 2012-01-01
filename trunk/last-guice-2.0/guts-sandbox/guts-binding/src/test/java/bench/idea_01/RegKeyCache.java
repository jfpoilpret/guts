package bench.idea_01;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("serial")
class RegKeyCache extends ConcurrentHashMap<RegKey, RegKey> {

	RegKey obtain(final Object instance, final Method method) {

		final RegKey newKey = new RegKey(instance, method);

		RegKey oldKey = get(newKey);

		if (oldKey == null) {
			putIfAbsent(newKey, newKey);
			oldKey = get(newKey);
			assert oldKey != null;
			assert oldKey.equals(newKey);
		}

		return oldKey;

	}

}
