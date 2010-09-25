package net.guts.event.example.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.guts.event.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class Supplier {

	static private final Logger log = LoggerFactory.getLogger(Supplier.class);

	private final Channel<Integer> integerChannel;

	private final ScheduledExecutorService service = Executors
			.newSingleThreadScheduledExecutor();

	@Inject
	public Supplier(Channel<Integer> channel) {

		integerChannel = channel;

		log.info("init");

		service.scheduleAtFixedRate(task, 1000, 1000, TimeUnit.MILLISECONDS);

	}

	private final Runnable task = new Runnable() {

		@Override
		public void run() {

			integerChannel.publish(10);

			log.info("publised");

		}

	};

	public void shutdown() {

		service.shutdownNow();

	}

}
