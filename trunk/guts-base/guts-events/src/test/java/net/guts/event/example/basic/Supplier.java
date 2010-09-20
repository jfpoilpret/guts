package net.guts.event.example.basic;

import net.guts.event.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class Supplier {

	static private final Logger log = LoggerFactory.getLogger(Supplier.class);

	private final Channel<Integer> integerChannel;

	@Inject
	Supplier(Channel<Integer> channel) {

		integerChannel = channel;

		log.info("init");

		integerChannel.publish(10);

		log.info("publised");

	}

}
