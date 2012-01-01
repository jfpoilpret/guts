package bench.idea_03.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.spi.InjectionListener;

public class SyncInjectListener implements InjectionListener<Object> {

	static private final Logger log = LoggerFactory
			.getLogger(SyncInjectListener.class);

	public SyncInjectListener() {

		log.debug("init");

	}

	@Override
	public void afterInjection(Object instance) {

		log.debug("after : {}", instance);

	}

}
