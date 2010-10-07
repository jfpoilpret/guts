package com.example.gui;

import java.applet.Applet;
import java.awt.Window;
import java.util.List;

import net.guts.event.EventModule;
import net.guts.gui.exit.ExitModule;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;
import net.guts.gui.session.Sessions;

import com.example.gui.session.StateApplet;
import com.example.gui.session.StateWindow;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class UserModules {

	public static void initModules(List<Module> moduleList) {

		moduleList.add(new ResourceModule());

		moduleList.add(new EventModule());

		moduleList.add(new ExitModule());

		moduleList.add(new AbstractModule() {

			@Override
			protected void configure() {

				binder().bind(MessageFactory.class);

				//

				Sessions.bindApplicationClass(binder(), //
						UserGui.class);

				Sessions.bindSessionConverter(binder(), //
						Applet.class).to(StateApplet.class);

				Sessions.bindSessionConverter(binder(), //
						Window.class).to(StateWindow.class);

				//

				Resources.bindRootBundle(binder(), //
						UserResources.class, UserResources.FILE_NAME);

			}

		});

	}

}
