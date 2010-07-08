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

package net.guts.gui.examples.addressbook.view;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.guts.event.Channel;
import net.guts.event.Event;
import net.guts.gui.examples.addressbook.business.AddressBookService;
import net.guts.gui.examples.addressbook.domain.Contact;

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

	//TODO constant for channel topic name!!!
	@Inject public ContactsListView(AddressBookService service, 
		final Channel<Contact> selectedContactChannel,
		final @Event(topic = "OpenContactPicture") Channel<Contact> openContactChannel)
	{
		setLayout(new BorderLayout());
		_contacts = GlazedLists.eventList(service.getAllContacts());
		String[] properties = {"firstName", "lastName", "home.city"};
		TableFormat<Contact> format = GlazedLists.tableFormat(
			Contact.class, properties, properties);
		_model = new EventTableModel<Contact>(_contacts, format);
		_table.setModel(_model);

		//TODO Table utilities to which you just pass a Channel<T> and they create and
		// register a new listener
		
		// Add selection listener to publish events
		_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent event)
			{
				int selected = _table.getSelectedRow();
				if (selected != _lastSelection)
				{
					_lastSelection = selected;
					selectedContactChannel.publish(getSelectedContact());
				}
			}
			
			private int _lastSelection = -1;
		});

		// Manage double-click: open a new ContactPicture tab
		_table.addMouseListener(new MouseAdapter()
		{
			@Override public void mouseClicked(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					openContactChannel.publish(getSelectedContact());
				}
			}
		});
		
		add(new JScrollPane(_table));
	}
	
	private Contact getSelectedContact()
	{
		int selected = _table.getSelectedRow();
		return (selected > -1 ? _model.getElementAt(selected) : null);
	}
	
	final private EventList<Contact> _contacts;
	final private EventTableModel<Contact> _model;
	final private JTable _table = new JTable();
}
