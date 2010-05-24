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

package net.guts.gui.examples.addressbook;

import java.util.List;

import net.guts.event.Events;
import net.guts.gui.application.AbstractAppLauncher;
import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.examples.addressbook.action.ContactActions;
import net.guts.gui.examples.addressbook.domain.Contact;
import net.guts.gui.examples.addressbook.util.TasksGroupProgressPanel;
import net.guts.gui.message.MessageModule;
import net.guts.gui.naming.ComponentNamePolicy;
import net.guts.gui.naming.ComponentNamingModule;
import net.guts.gui.naming.DefaultComponentNamePolicy;
import net.guts.gui.resource.Resources;
import net.guts.gui.task.blocker.BlockerDialogPane;

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
		// Finally, add modules we use from guts-gui
		modules.add(new MessageModule());
		modules.add(new ComponentNamingModule());
		// Finally, add our specific module
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
			Resources.bindRootBundle(binder(), getClass(), "resources");

			// Set our own blocker dialog panel
			bind(BlockerDialogPane.class).to(TasksGroupProgressPanel.class);
			
			// Set our own component naming policy
			bind(ComponentNamePolicy.class).toInstance(new AddressBookNamePolicy());
			
			//TODO remove after resource injection tests and performance comparison
			Resources.bindInjectionStrategy(binder()).to(SimpleInjectionDecisionStrategy.class);
		}
	}

	// Special policy for automatically naming Swing components:
	// Don't use "-" as a name separator, since all fields names already start with "_"
	static private class AddressBookNamePolicy extends DefaultComponentNamePolicy
	{
		@Override protected String separator()
		{
			return "";
		}
	}
}
