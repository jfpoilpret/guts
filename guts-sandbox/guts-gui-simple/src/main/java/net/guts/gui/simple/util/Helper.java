package net.guts.gui.simple.util;

import java.awt.Component;
import java.awt.Container;
import java.util.List;

import net.guts.gui.message.MessageFactory;
import net.guts.gui.simple.Guts;
import net.guts.gui.simple.message.MessageFactoryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class Helper {

	static private final Logger log = LoggerFactory.getLogger(Guts.class);

	public static void logTree(Component child) {

		log.debug("child : {}", child);

		for (Container parent = child.getParent(); parent != null; parent = parent
				.getParent()) {

			log.debug("parent : {}", parent);

		}

	}

	public static void initModules(List<Module> moduleList) {

		Module hackModule = new AbstractModule() {
			@Override
			protected void configure() {

				bind(MessageFactory.class).to(MessageFactoryImpl.class);

			}
		};

	}

}
