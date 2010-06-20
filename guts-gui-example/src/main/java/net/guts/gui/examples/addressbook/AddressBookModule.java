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

import net.guts.event.Events;
import net.guts.gui.action.ActionNamePolicy;
import net.guts.gui.action.DefaultActionNamePolicy;
import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.examples.addressbook.action.ContactActions;
import net.guts.gui.examples.addressbook.domain.Contact;
import net.guts.gui.examples.addressbook.util.TasksGroupProgressPanel;
import net.guts.gui.naming.ComponentNamePolicy;
import net.guts.gui.naming.DefaultComponentNamePolicy;
import net.guts.gui.resource.Resources;
import net.guts.gui.task.TaskInfo;
import net.guts.gui.task.blocker.BlockerDialogPane;
import net.guts.gui.util.EnumIconRenderer;

import com.google.inject.AbstractModule;

class AddressBookModule extends AbstractModule
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
		Resources.bindEnumConverter(binder(), TaskInfo.State.class);
		EnumIconRenderer.bind(binder(), TaskInfo.State.class);

		// Set our own blocker dialog panel
		bind(BlockerDialogPane.class).to(TasksGroupProgressPanel.class);
		
		// Set our own component naming policy
		bind(ComponentNamePolicy.class).toInstance(new AddressBookComponentNamePolicy());
		bind(ActionNamePolicy.class).toInstance(new AddressBookActionNamePolicy());
	}
}

// Special policy for automatically naming Swing components:
// Don't use "-" as a name separator, since all fields names already start with "_"
class AddressBookComponentNamePolicy extends DefaultComponentNamePolicy
{
	@Override protected String separator()
	{
		return "";
	}
}

class AddressBookActionNamePolicy extends DefaultActionNamePolicy
{
	@Override protected String separator()
	{
		return "";
	}
}
