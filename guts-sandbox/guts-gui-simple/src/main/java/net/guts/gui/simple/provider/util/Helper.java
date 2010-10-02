package net.guts.gui.simple.provider.util;

import java.awt.Component;
import java.awt.Container;

import net.guts.gui.simple.provider.Guts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {

	static private final Logger log = LoggerFactory.getLogger(Guts.class);

	public static void logTree(Component child) {

		log.debug("child : {}", child);

		for (Container parent = child.getParent(); parent != null; parent = parent
				.getParent()) {

			log.debug("parent : {}", parent);

		}

	}

}
