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

package net.guts.gui.examples.addressbook.docking;

import javax.swing.JComponent;

import net.guts.gui.application.docking.EmptyableViewport;
import net.guts.gui.application.docking.ViewContentFactory;
import net.guts.gui.examples.addressbook.view.ContactDetailView;
import net.guts.gui.examples.addressbook.view.ContactPictureView;
import net.guts.gui.examples.addressbook.view.ContactsListView;

import com.google.inject.Inject;
import com.google.inject.Injector;

//TODO provide a default ViewContentFactory that is parameterized with 
// a Map<String, Class<? extends JComponent>>
public class AddressBookViewFactory implements ViewContentFactory
{
	@Inject AddressBookViewFactory(Injector injector)
	{
		_injector = injector;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.docking.ViewContentFactory#createContent(java.lang.String)
	 */
	@Override public JComponent createContent(String id)
	{
		if (EmptyableViewport.EMPTY_VIEW_ID.equals(id))
		{
			return _injector.getInstance(ContactPictureView.class);
		}
		else if (Views.ContactList.name().equals(id))
		{
			return _injector.getInstance(ContactsListView.class);
		}
		else if (Views.ContactDetail.name().equals(id))
		{
			return _injector.getInstance(ContactDetailView.class);
		}
		else
		{
			return null;
		}
	}

	final private Injector _injector;
}
