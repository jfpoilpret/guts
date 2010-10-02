package com.example.gui;

import java.util.List;

import net.guts.event.EventModule;
import net.guts.gui.exit.ExitModule;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;
import net.guts.gui.session.SessionModule;
import net.guts.gui.session.Sessions;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class UserModules {

	static void initModules(List<Module> moduleList) {

		moduleList.add(new ResourceModule());

		moduleList.add(new SessionModule());

		moduleList.add(new EventModule());

		moduleList.add(new ExitModule());

		moduleList.add(new AbstractModule() {

			@Override
			protected void configure() {

				Sessions.bindApplicationClass(binder(), //
						UserGui.class);

				Resources.bindRootBundle(binder(), //
						UserResources.class, UserResources.FILE_NAME);

			}

		});

	}

}
