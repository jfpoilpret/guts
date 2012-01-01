package com.example.gui;

import java.util.List;

import javax.swing.JApplet;

import net.guts.gui.exit.ExitController;
import net.guts.gui.simple.Guts;
import net.guts.gui.simple.GutsRoot;
import net.guts.gui.simple.application.AP_WindowController;
import net.guts.gui.simple.util.Helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Module;

@SuppressWarnings("serial")
public class UserApplet extends JApplet implements GutsRoot<JApplet> {

	static private final Logger log = LoggerFactory.getLogger(UserApplet.class);

	@Override
	public void init() {
		Guts.inject(this);
	}

	@Override
	public JApplet getRoot() {
		return this;
	}

	@Override
	public void initModules(List<Module> moduleList) {
		UserModules.initModules(moduleList);
	}

	@Inject
	void initGUI(final UserGui gui, //
			final AP_WindowController windowController, //
			final ExitController exitController) {

		JApplet root = getRoot();

		root.setName(UserResources.ROOT_NAME);
		root.add(gui);

		windowController.show(root);

		log.info("ready");

		Helper.logTree(root);

	}

}
