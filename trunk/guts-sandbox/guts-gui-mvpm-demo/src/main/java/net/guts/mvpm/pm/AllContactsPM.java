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

package net.guts.mvpm.pm;

import static net.guts.binding.GutsTableModelBuilder.newTableModelFor;

import java.util.Date;

import javax.swing.table.TableModel;

import net.guts.binding.GutsPresentationModel;
import net.guts.binding.GutsTableModelBuilder;
import net.guts.binding.Models;
import net.guts.gui.resource.InjectResources;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.mvpm.business.AddressBookService;
import net.guts.mvpm.domain.Address;
import net.guts.mvpm.domain.Contact;
import net.guts.properties.Bean;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;

@Singleton
@InjectResources(autoUpdate = true)
public class AllContactsPM
{
	@Inject
	AllContactsPM(AddressBookService service, ContactPMFactory contactPMFactory)
	{
		this.service = service;
		this.contactPMFactory = contactPMFactory;

		// Create all necessary models here
		ValueModel<Contact> selection = Models.holder(true);
		contacts = new SelectionInList<Contact>(service.getAllContacts(), selection);
		selectionPM = newContactPM(selection);
		GutsPresentationModel<Contact> selectionModel = Models.createPM(Contact.class, selection);
		
		Contact of = Models.of(Contact.class);
		selectionFirstName = selectionModel.getPropertyModel(of.getFirstName());
		selectionLastName = selectionModel.getPropertyModel(of.getLastName());
		selectionBirth = selectionModel.getPropertyModel(of.getBirth());
		selectionCompactAddress = new CompactAddressModel(
			selectionModel.getPropertyModel(of.getHome()));
		title = new TitleConverter(selection);
		
		GutsTableModelBuilder<Contact> builder = newTableModelFor(Contact.class);
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

	// Strangely this class must be public or JGoodies Bindings will throw an exception...
	@SuppressWarnings("serial") 
	public class TitleConverter extends AbstractConverter<Contact, String>
	{
		TitleConverter(ValueModel<Contact> subject)
		{
			super(subject);
		}

		@Override public void setValue(String unused)
		{
			// Nothing to do, this converter is one way only
		}

		@Override public String convertFromSubject(Contact contact)
		{
			if (contact != null)
			{
				return String.format(
					titleWithContactFormat, contact.getFirstName(), contact.getLastName());
			}
			else
			{
				return titleWithoutContact;
			}
		}
	}
	
	final public SelectionInList<Contact> contacts;
	final public TableModel contactsTableModel;
	final public ContactPM selectionPM;
	final public ValueModel<String> title;

	final public ValueModel<String> selectionFirstName;
	final public ValueModel<String> selectionLastName;
	final public ValueModel<Date> selectionBirth;
	final public ValueModel<String> selectionCompactAddress;
	
	final private AddressBookService service;
	final private ContactPMFactory contactPMFactory;

	// Injected as resource
	private String titleWithContactFormat;
	private String titleWithoutContact;
}
