package com.example.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserResources {

	static private final Logger log = LoggerFactory
			.getLogger(UserResources.class);

	public static final String ROOT_NAME = "root-name";

	public static final String FILE_NAME = "resources";

	static {
		log.info("NOTE: " + FILE_NAME + ".properties should be located in {}",
				UserResources.class.getPackage().getName());
	}

}
