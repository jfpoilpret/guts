package net.guts.gui.simple;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.RootPaneContainer;

import net.guts.common.injection.InjectionListeners;
import net.guts.gui.simple.util.Helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class Guts {

	static private final Logger log = LoggerFactory.getLogger(Guts.class);

	public static <R extends RootPaneContainer> void inject(
			final GutsRoot<R> prog) {

		final Runnable launchTask = new Runnable() {

			@Override
			public void run() {

				RootPaneContainer root = prog.getRoot();

				log.debug("prog : {}", prog);
				log.debug("root : {}", root);

				List<Module> moduleList = new ArrayList<Module>();

				// hacks
				Helper.initModules(moduleList);

				// users
				prog.initModules(moduleList);

				Injector injector = Guice.createInjector(moduleList);

				InjectionListeners.injectListeners(injector);

				injector.injectMembers(prog);

			}

		};

		try {
			EventQueue.invokeAndWait(launchTask);
		} catch (Exception e) {
			log.error("", e);
		}

		log.debug("done");

	}

}
