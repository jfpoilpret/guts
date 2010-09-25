package bench.idea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	static private final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		log.info("started");

		Injector injector = Guice.createInjector(new BeanBindModule());

		BeanOne b1 = injector.getInstance(BeanOne.class);
		BeanTwo b2 = injector.getInstance(BeanTwo.class);

		BeanBindRegistry registry = injector
				.getInstance(BeanBindRegistry.class);

		registry.bind(b1, b2);

		b1.setOne(10);

		b2.setTwo(20);

		log.info("finished");

	}
}
