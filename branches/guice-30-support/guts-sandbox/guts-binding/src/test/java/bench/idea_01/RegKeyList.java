package bench.idea_01;

import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
class RegKeyList extends CopyOnWriteArrayList<RegKey> {

	static private final Logger log = LoggerFactory.getLogger(RegKeyList.class);

	void invokeSuper(final Object[] args) {

		for (final RegKey regKey : this) {

			regKey.invokeSuper(args);

		}

	}

}
