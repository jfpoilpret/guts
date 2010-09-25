package bench.idea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Singleton;

@Singleton
public class BeanBindRegistry {

	static private final Logger log = LoggerFactory
			.getLogger(BeanBindRegistry.class);

	final BiMap<Object, Object> dirMap = HashBiMap.create();

	final BiMap<Object, Object> invMap = dirMap.inverse();

	BeanBindRegistry() {

		log.info("init");

	}

	void bind(Object one, Object two) {

		log.info("one : {}", one);
		log.info("two : {}", two);

		dirMap.put(one, two);

	}

	Object find(Object any) {

		Object one = dirMap.get(any);

		if (one != null) {
			return one;
		}

		Object two = invMap.get(any);

		if (two != null) {
			return two;
		}

		log.error("not bound : {}", any);

		return null;

	}

}
