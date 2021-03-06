package net.guts.demo.event_bus.example.concurrent;

import net.guts.event.Consumes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {

	static private final Logger log = LoggerFactory.getLogger(Consumer.class);

	Consumer() {

		log.info("init");

	}

	@Consumes
	public void accept(Integer event) {

		log.info("accepted {}", event);

	}

}
