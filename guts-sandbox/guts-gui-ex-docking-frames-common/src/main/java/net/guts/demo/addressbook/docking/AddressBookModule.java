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

package net.guts.demo.addressbook.docking;

import net.guts.demo.addressbook.docking.action.ContactActions;
import net.guts.demo.addressbook.docking.docking.AddressBookLayout;
import net.guts.demo.addressbook.docking.docking.Views;
import net.guts.demo.addressbook.docking.domain.Contact;
import net.guts.demo.addressbook.docking.view.ContactDetailView;
import net.guts.demo.addressbook.docking.view.ContactsListView;
import net.guts.event.Events;
import net.guts.gui.action.ActionNamePolicy;
import net.guts.gui.action.DefaultActionNamePolicy;
import net.guts.gui.application.AppLifecycleStarter;

import static net.guts.demo.addressbook.docking.action.ContactActions.OPEN_CONTACT_PICT_TOPIC;

import net.guts.gui.docking.Docking;
import net.guts.gui.naming.ComponentNamePolicy;
import net.guts.gui.naming.DefaultComponentNamePolicy;
import net.guts.gui.resource.Resources;

import com.google.inject.AbstractModule;

class AddressBookModule extends AbstractModule
{
	@Override protected void configure()
	{
		bind(AppLifecycleStarter.class).to(AddressBookLifecycle.class)
			.asEagerSingleton();
		
		// Docking setup
		Docking.bindDefaultLayout(binder()).to(AddressBookLayout.class);
		Docking.bindView(binder(), Views.ContactList.name(), ContactsListView.class);
		Docking.bindView(binder(), Views.ContactDetail.name(), ContactDetailView.class);
		Docking.bindDefaultContentArea(binder());
		Docking.bindWorkingArea(binder(), Views.ContactPicture.name());

		// Add binding for "contact selection" and "picture open" events
		Events.bindChannel(binder(), Contact.class);
		Events.bindChannel(binder(), Contact.class, OPEN_CONTACT_PICT_TOPIC);
		bind(ContactActions.class).asEagerSingleton();

		// Setup ResourceModule root bundle
		Resources.bindRootBundle(binder(), getClass(), "resources");

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
