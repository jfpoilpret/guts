package bench.idea_01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	static private final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		log.info("started");

		final Injector injector = Guice.createInjector(new BeanBindModule());

		final BeanOne b1 = injector.getInstance(BeanOne.class);
		final BeanTwo b2 = injector.getInstance(BeanTwo.class);

		final BeanBindRegistry registry = injector
				.getInstance(BeanBindRegistry.class);

		registry.bind(b1, b2);

		b1.setRedOne(10);
		b2.setRedTwo(20);

		registry.bind(b1, b2, BeanBindKey.KEY_GREEN);

		b1.setGreenOne(11);
		b2.setGreenTwo(21);

		log.info("finished");

	}

}
