package bench.idea_02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bench.idea_02.api.BindId;
import bench.idea_02.api.BindRegistry;
import bench.idea_02.provider.BindModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	static private final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		log.info("started");

		final Injector injector = Guice.createInjector(new BindModule());

		final BeanOne b1 = injector.getInstance(BeanOne.class);
		final BeanTwo b2 = injector.getInstance(BeanTwo.class);

		final BindRegistry registry = injector.getInstance(BindRegistry.class);

		log.info("one-way bind");

		registry.bind(b1, b2, BindId.KEY_DEFAULT);

		b1.setRedOne(10);
		b2.setRedTwo(20);

		log.info("two-way bind");

		registry.bindBoth(b1, b2, BindId.KEY_GREEN);

		b1.setGreenOne(11);
		b2.setGreenTwo(21);

		log.info("one-way bind");

		registry.bind(b2, b1, BindId.KEY_ORANGE);

		b2.setGreenTwo(32, "three-two");
		b1.setGreenOne(31, "three-one");

		log.info("two-way bind");

		registry.bindBoth(b1, b2, BindId.KEY_0);

		b1.setIntOne(11);
		b2.setIntTwo(21);

		log.info("finished");

	}

}
