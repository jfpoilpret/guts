//  Copyright 2009 Jean-Francois Poilpret
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.guts.mvpm;

import java.util.List;

import net.guts.gui.application.AbstractApplication;
import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.message.MessageModule;
import net.guts.gui.naming.ComponentNamingModule;
import net.guts.gui.resource.Resources;
import net.guts.gui.template.okcancel.OkCancelModule;
import net.guts.mvpm.pm.ContactPM;
import net.guts.mvpm.pm.ContactPMFactory;
import net.guts.mvpm.view.ContactView;
import net.guts.mvpm.view.ContactViewFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryProvider;

public class AddressBookMain extends AbstractApplication
{
	public static void main(String[] args)
	{
		new AddressBookMain().launch(args);
	}
	
	@Override protected void initModules(String[] args, List<Module> modules)
	{
		// Workaround Sun bug #5079688 with JFrame/JDialog resize on Windows
		System.setProperty("sun.awt.noerasebackground", "true");
		// Finally, add modules we use from guts-gui
		modules.add(new MessageModule());
		modules.add(new ComponentNamingModule());
		modules.add(new OkCancelModule());
		// Finally, add our specific module
		modules.add(new AbstractModule()
		{
			@Override protected void configure()
			{
				// Setup ResourceModule root bundle
				Resources.bindRootBundle(binder(), getClass(), "resources");

				// Most important: bind the AppLifecycleStarter
				bind(AppLifecycleStarter.class)
					.to(AddressBookLifecycle.class).asEagerSingleton();
				
				// Bind the factory for ContactViews
				bind(ContactViewFactory.class).toProvider(
					FactoryProvider.newFactory(ContactViewFactory.class, ContactView.class));
				bind(ContactPMFactory.class).toProvider(
					FactoryProvider.newFactory(ContactPMFactory.class, ContactPM.class));
			}
		});
	}
}
