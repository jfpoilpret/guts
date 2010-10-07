package com.example.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JFrame;

import net.guts.gui.exit.ExitController;
import net.guts.gui.simple.api.GutsRoot;
import net.guts.gui.simple.provider.Guts;
import net.guts.gui.simple.provider.application.AP_WindowController;
import net.guts.gui.simple.provider.util.Helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Module;

public class UserApp implements GutsRoot<JFrame> {

	static private final Logger log = LoggerFactory.getLogger(UserApp.class);

	public static void main(String[] args) {
		Guts.inject(new UserApp());
	}

	private final JFrame root = new JFrame();

	@Override
	public JFrame getRoot() {
		return root;
	}

	@Override
	public void initModules(List<Module> moduleList) {
		UserModules.initModules(moduleList);
	}

	@Inject
	public void initGUI(final UserGui gui, //
			final AP_WindowController windowController, //
			final ExitController exitController) {

		JFrame root = getRoot();

		root.setName(UserResources.ROOT_NAME);
		root.add(gui);

		root.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitController.shutdown();
			}
		});

		windowController.show(root);

		log.info("ready");

		Helper.logTree(root);

	}

}
