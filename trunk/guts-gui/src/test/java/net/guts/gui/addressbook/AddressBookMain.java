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

package net.guts.gui.addressbook;

import java.util.List;

import net.guts.event.Events;
import net.guts.gui.addressbook.action.ContactActions;
import net.guts.gui.addressbook.domain.Contact;
import net.guts.gui.application.AbstractAppLauncher;
import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.message.MessageModule;
import net.guts.gui.resource.Resources;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class AddressBookMain extends AbstractAppLauncher
{
	public static void main(String[] args)
	{
		new AddressBookMain().launch(args);
	}
	
	@Override protected void initModules(String[] args, List<Module> modules)
	{
		// Make sure frames & dialogs are decorated by the current PLAF (substance)
//		JFrame.setDefaultLookAndFeelDecorated(true);
//		JDialog.setDefaultLookAndFeelDecorated(true);
		// Workaround Sun bug #5079688 with JFrame/JDialog resize on Windows
		System.setProperty("sun.awt.noerasebackground", "true");
		// Enable automatic content selection on focus gained for all text fields
//		UIManager.put(LafWidget.TEXT_SELECT_ON_FOCUS, Boolean.TRUE);
		// Finally, add our specific module
		modules.add(new MessageModule());
		modules.add(new AddressBookModule());
	}

	private class AddressBookModule extends AbstractModule
	{
		@Override protected void configure()
		{
			bind(AppLifecycleStarter.class)
				.to(AddressBookLifecycleStarter.class).asEagerSingleton();
			// Add binding for "contact selection" events
			Events.bindChannel(binder(), Contact.class);
			bind(ContactActions.class).asEagerSingleton();
			// Setup ResourceModule root bundle
			//TODO improve API by adding 2nd bindRootBundle() that takes Class as ref. directory?
			Resources.bindRootBundle(binder(), "/net/guts/gui/addressbook/resources");
		}
	}
}