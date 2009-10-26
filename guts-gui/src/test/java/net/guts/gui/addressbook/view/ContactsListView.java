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

package net.guts.gui.addressbook.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.guts.event.Channel;
import net.guts.gui.addressbook.business.AddressBookService;
import net.guts.gui.addressbook.domain.Contact;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

@Singleton
public class ContactsListView extends JPanel
{
	static final private long serialVersionUID = 7068262166438989381L;
	//#### Put that elsewhere (where?)
	static final public String SELECTED_CONTACT = "SelectedContact";
	static final private String NAME = "ContactsListView";
	
	@Inject public ContactsListView(
		AddressBookService service, final Channel<Contact> selectedContactChannel)
	{
		setLayout(new BorderLayout());
		setName(NAME);
		_table.setName(NAME + "-list");

		_contacts = GlazedLists.eventList(service.getAllContacts());
		String[] properties = {"firstName", "lastName", "home.city"};
		TableFormat<Contact> format = GlazedLists.tableFormat(
			Contact.class, properties, properties);
		final EventTableModel<Contact> model =
			new EventTableModel<Contact>(_contacts, format);
		_table.setModel(model);
		
		// Add selection listener to publish events
		_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent event)
			{
				int selected = _table.getSelectedRow();
				if (selected != _lastSelection)
				{
					Contact row = null;
					if (selected > -1)
					{
						row = model.getElementAt(selected);
					}
					_lastSelection = selected;
					selectedContactChannel.publish(row);
				}
			}
			
			private int _lastSelection = -1;
		});
		
		add(new JScrollPane(_table));
	}
	
	final private EventList<Contact> _contacts;
	final private JTable _table = new JTable();
}
