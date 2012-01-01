package bench.idea_02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bench.idea_02.api.BindId;
import bench.idea_02.api.BindRegistry;
import bench.idea_02.provider.BindModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestWeakRefs {

	static private final Logger log = LoggerFactory
			.getLogger(TestWeakRefs.class);

	static final int CYCLE = 100;
	static final int COUNT = 100000;

	static void test(final Injector injector) {

		final BeanOne b1 = injector.getInstance(BeanOne.class);
		final BeanTwo b2 = injector.getInstance(BeanTwo.class);

		final BindRegistry registry = injector.getInstance(BindRegistry.class);

		registry.bind(b1, b2, BindId.KEY_DEFAULT);

		b1.setRedOne(10);
		b2.setRedTwo(20);

		registry.bindBoth(b1, b2, BindId.KEY_GREEN);

		b1.setGreenOne(11);
		b2.setGreenTwo(21);

	}

	static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		log.info("started");

		final Injector injector = Guice.createInjector(new BindModule());

		for (int k = 0; k < CYCLE; k++) {

			for (int m = 0; m < COUNT; m++) {

				test(injector);

			}

			System.gc();

			sleep(1000);

			log.info("count={}", k);

		}

		log.info("finished");

	}

}
