package net.guts.demo.event_bus.example.basic;

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

		Injector injector = Guice.createInjector(new EventModule(),
				new AbstractModule() {
					@Override
					protected void configure() {
						Events.bindChannel(binder(), Integer.class);
					}
				});

		InjectionListeners.injectListeners(injector);

		Consumer conusmer = injector.getInstance(Consumer.class);

		Supplier supplier = injector.getInstance(Supplier.class);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.info("finished");

	}

}
