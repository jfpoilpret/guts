package net.guts.demo.event_bus.example.concurrent;

import net.guts.common.injection.InjectionListeners;
import net.guts.event.EventModule;
import net.guts.event.Events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	static private final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {

		log.info("started");

		final Injector injector = Guice.createInjector(new EventModule(),
				new AbstractModule() {
					@Override
					protected void configure() {
						Events.bindChannel(binder(), Integer.class);
					}
				});

		InjectionListeners.injectListeners(injector);

		final Consumer consumer = injector.getInstance(Consumer.class);

		final Supplier supplier = injector.getInstance(Supplier.class);

		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			log.error("unexpected", e);
		}

		supplier.shutdown();

		log.info("finished");

	}

}
