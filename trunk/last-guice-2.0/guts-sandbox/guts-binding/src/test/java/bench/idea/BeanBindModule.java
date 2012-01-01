package bench.idea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

class BeanBindModule extends AbstractModule {

	static private final Logger log = LoggerFactory
			.getLogger(BeanBindModule.class);

	BeanBindModule() {
		log.info("init");
	}

	@Override
	protected void configure() {

		BeanBindInterceptor interceptor = new BeanBindInterceptor();

		binder().requestInjection(interceptor);

		bindInterceptor(//
				Matchers.any(), //
				Matchers.annotatedWith(BeanBind.class), //
				interceptor//
		);

		log.info("configured; interceptor : {}", interceptor);

	}

}