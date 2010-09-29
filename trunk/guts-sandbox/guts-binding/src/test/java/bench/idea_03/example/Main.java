package bench.idea_03.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bench.idea_03.api.SyncTopic;
import bench.idea_03.api.Registry;
import bench.idea_03.provider.SyncModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	static private final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		log.info("started");

		final Injector injector = Guice.createInjector(new SyncModule());

		final BeanOne b1 = injector.getInstance(BeanOne.class);
		final BeanTwo b2 = injector.getInstance(BeanTwo.class);

		final Registry registry = injector.getInstance(Registry.class);

		log.info("one-way publish");

		registry.publish(b1, b2, SyncTopic.KEY_DEFAULT);

		b1.setRedOne(10);
		b2.setRedTwo(20);

		log.info("two-way replicate");

		registry.replicate(b1, b2, SyncTopic.KEY_GREEN);

		b1.setGreenOne(11);
		b2.setGreenTwo(21);

		log.info("one-way publish");

		registry.publish(b2, b1, SyncTopic.KEY_ORANGE);

		b2.setGreenTwo(32, "three-two");
		b1.setGreenOne(31, "three-one");

		log.info("two-way replicate");

		registry.replicate(b1, b2, SyncTopic.KEY_0);

		b1.setIntOne(11);
		b2.setIntTwo(21);

		log.info("finished");

	}

}
