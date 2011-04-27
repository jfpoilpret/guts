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

package net.guts.mvp.presenter;

import static net.guts.binding.GutsTableModelBuilder.newTableModelFor;

import javax.swing.table.TableModel;

import net.guts.binding.GutsTableModelBuilder;
import net.guts.binding.Models;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.mvp.business.AddressBookService;
import net.guts.mvp.domain.Address;
import net.guts.mvp.domain.Contact;
import net.guts.properties.Bean;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;

@Singleton
public class AllContactsPM
{
	@Inject
	AllContactsPM(AddressBookService service, ContactPMFactory contactPMFactory)
	{
		this.service = service;
		this.contactPMFactory = contactPMFactory;

		// Create all necessary models here
		ValueModel<Contact> selectionModel = Models.holder(true);
		contacts = new SelectionInList<Contact>(service.getAllContacts(), selectionModel);
		selection = newContactPM(selectionModel);
		
		GutsTableModelBuilder<Contact> builder = newTableModelFor(Contact.class);
		Contact of = builder.of();
		builder.addProperty(of.getFirstName())
				.addProperty(of.getLastName())
				.addProperty(of.getBirth())
				.addProperty(of.getHome().getCity());
		contactsTableModel = builder.buildModel();
	}
	
	public Task<?> getDeleteTask()
	{
		final Contact selection = contacts.getSelection();
		return new Task<Void>()
		{
			@Override 
			public Void execute(FeedbackController controller) throws InterruptedException
			{
				service.removeContact(selection);
				return null;
			}
		};
	}
	
	public ContactPM createContact()
	{
		Contact blank = new Contact();
		blank.setHome(Bean.create(Address.class).proxy(blank.getHome()));
		blank.setOffice(Bean.create(Address.class).proxy(blank.getOffice()));
		blank = Bean.create(Contact.class).proxy(blank);
		return newContactPM(Models.holderFor(blank, true));
	}
	
	private ContactPM newContactPM(ValueModel<Contact> model)
	{
		return contactPMFactory.create(model);
	}

	public GutsAction saveContact(final ContactPM contact)
	{
		// Note: it is necessary to name the action because auto-naming can't work with
		// actions that are created "on the fly"
		return new TaskAction("saveContact")
		{
			@Override protected void perform()
			{
				submit(new Task<Void>()
					{
						@Override public Void execute(FeedbackController controller) throws Exception
						{
							service.createContact(contact.contactModel().getValue());
							return null;
						}
					});
			}
		};
	}
	
	final public GutsAction saveSelectedContact = new TaskAction()
	{
		@Override protected void perform()
		{
			final Contact selection = contacts.getSelection();
			submit(new Task<Void>()
			{
				@Override public Void execute(FeedbackController controller) throws Exception
				{
					// Check if new contact
					if (selection.getId() == 0)
					{
						service.createContact(selection);
					}
					else
					{
						service.modifyContact(selection);
					}
					return null;
				}
			});
		}
	};
	
	final public SelectionInList<Contact> contacts;
	final public TableModel contactsTableModel;
	final public ContactPM selection;
	
	final private AddressBookService service;
	final private ContactPMFactory contactPMFactory;
}
