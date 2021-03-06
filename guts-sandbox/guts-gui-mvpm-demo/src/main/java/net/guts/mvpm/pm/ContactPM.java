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

import static net.guts.gui.validation.ValidationHelper.checkMandatory;
import static net.guts.gui.validation.ValidationHelper.isEmpty;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.Validatable;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;

@InjectResources(autoUpdate = true)
public class ContactPM implements Validatable
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
		title = new TitleConverter(contact);
		
		// Connect save action with dirtiness of buffered models
		model.addPropertyChangeListener(
			PresentationModel.PROPERTYNAME_BUFFERING, dirtyListener);
		homeAddress.address.addPropertyChangeListener(
			PresentationModel.PROPERTYNAME_BUFFERING, dirtyListener);
		officeAddress.address.addPropertyChangeListener(
			PresentationModel.PROPERTYNAME_BUFFERING, dirtyListener);

		save.setEnabled(false);
	}
	
	ValueModel<Contact> contactModel()
	{
		return model.getBeanChannel();
	}

	// Normally we need to listen to changes in getBeanChannel and propagate changes 
	// to listeners, but not required here because picture cannot change.
	// Strangely this class must be public or JGoodies Bindings will throw an exception...
	@SuppressWarnings("serial") 
	public class PictureModel extends AbstractValueModel<Icon>
	{
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
	
	@Override public ValidationResult validate()
	{
		ValidationResult result = new ValidationResult();
		// Check mandatory fields first
		checkMandatory(firstName, result, ContactValidationKeys.KEY_MANDATORY_FIRST_NAME);
		checkMandatory(lastName, result, ContactValidationKeys.KEY_MANDATORY_LAST_NAME);
		// Check address is complete enough when some fields have been filled in
		checkAddress(homeAddress, result, ContactValidationKeys.KEY_PREFIX_HOME);
		checkAddress(officeAddress, result, ContactValidationKeys.KEY_PREFIX_OFFICE);
		validation.setResult(result);
		return result;
	}
	
	static private void checkAddress(AddressPM address, ValidationResult result, String key)
	{
		if ((!isEmpty(address.street1)) || (!isEmpty(address.street2)))
		{
			checkMandatory(
				address.city, result, key + ContactValidationKeys.KEY_SUFFIX_MISSING_CITY);
			checkMandatory(
				address.zip, result, key + ContactValidationKeys.KEY_SUFFIX_MISSING_ZIP);
		}
		else if (isEmpty(address.city) != isEmpty(address.zip))
		{
			checkMandatory(
				address.city, result, key + ContactValidationKeys.KEY_SUFFIX_MISSING_CITY);
			checkMandatory(
				address.zip, result, key + ContactValidationKeys.KEY_SUFFIX_MISSING_ZIP);
		}
	}
	
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
			if (contact != null && contact.getId() != 0)
			{
				return String.format(
					titleContactFormat, contact.getFirstName(), contact.getLastName());
			}
			else
			{
				return titleNewContact;
			}
		}
	}

	final private PropertyChangeListener dirtyListener = new PropertyChangeListener()
	{
		@Override public void propertyChange(PropertyChangeEvent evt)
		{
			// Find out from all models if data needs to be saved
			boolean isBuffering = model.isBuffering();
			isBuffering |= homeAddress.address.isBuffering();
			isBuffering |= officeAddress.address.isBuffering();
			save.setEnabled(isBuffering);
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
	final public ValueModel<String> title;

	final public ValidationResultModel validation = new DefaultValidationResultModel();

	// Injected as resource
	private Icon noPicture;
	private String titleContactFormat;
	private String titleNewContact;
}
