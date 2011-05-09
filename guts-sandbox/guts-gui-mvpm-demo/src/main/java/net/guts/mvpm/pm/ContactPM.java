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

import java.util.Date;

import javax.swing.Icon;

import net.guts.binding.GutsPresentationModel;
import net.guts.binding.Models;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.resource.InjectResources;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.mvpm.business.AddressBookService;
import net.guts.mvpm.domain.Contact;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

//TODO also add title for detail
@InjectResources(autoUpdate = true)
public class ContactPM
{
	@Inject ContactPM(@Assisted ValueModel<Contact> contact, AddressBookService service)
	{
		this.service = service;
		
		// Create all necessary models here
		model = Models.createPM(Contact.class, contact);
		
		Contact of = model.of();
		firstName = model.getBufferedModel(of.getFirstName());
		lastName = model.getBufferedModel(of.getLastName());
		birth = model.getBufferedModel(of.getBirth());
		
		homeAddress = new AddressPM(model.getPropertyModel(of.getHome()));
		officeAddress = new AddressPM(model.getPropertyModel(of.getOffice()));
		picture = new PictureModel();
	}
	
	ValueModel<Contact> contactModel()
	{
		return model.getBeanChannel();
	}

	//TODO Normally we need to listen to changes in getBeanChannel and propagate changes 
	// to listeners, but not required here because picture cannot change.
	// Strangely this class must be public or JGoodies Bindings will throw an exception...
	public class PictureModel extends AbstractValueModel<Icon>
	{
		private static final long serialVersionUID = -5066559085812900483L;

		PictureModel()
		{
		}
		
		@Override public Icon getValue()
		{
			Contact contact = model.getBeanChannel().getValue();
			Icon picture = null;
			if (contact != null)
			{
				picture = service.getContactPicture(contact.getId());
				if (picture == null)
				{
					// Replace with a default image that says "no image"
					picture = noPicture;
				}
			}
			return picture;
		}

		@Override public void setValue(Icon picture)
		{
			// Do nothing, current AddressBookService has no way to change contact picture
		}
	}

	final public GutsAction cancel = new GutsAction()
	{
		@Override protected void perform()
		{
			model.triggerFlush();
			homeAddress.address.triggerFlush();
			officeAddress.address.triggerFlush();
		}
	};
	
	final public GutsAction save = new TaskAction()
	{
		@Override protected void perform()
		{
			model.triggerCommit();
			homeAddress.address.triggerCommit();
			officeAddress.address.triggerCommit();
			final Contact selection = model.getBeanChannel().getValue();
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

	final private AddressBookService service;
	final private GutsPresentationModel<Contact> model;
	final public ValueModel<String> firstName;
	final public ValueModel<String> lastName;
	final public ValueModel<Date> birth;
	final public AddressPM homeAddress;
	final public AddressPM officeAddress;
	final public ValueModel<Icon> picture;

	// Injected as resource
	private Icon noPicture;
}
