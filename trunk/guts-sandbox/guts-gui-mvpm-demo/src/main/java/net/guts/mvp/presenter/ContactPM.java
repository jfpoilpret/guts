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

import java.util.Date;

import javax.swing.Icon;

import net.guts.binding.GutsPresentationModel;
import net.guts.binding.Models;
import net.guts.gui.resource.ResourceInjector;
import net.guts.mvp.business.AddressBookService;
import net.guts.mvp.domain.Contact;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class ContactPM
{
	@Inject ContactPM(@Assisted ValueModel<Contact> contact, 
		ResourceInjector injector, AddressBookService service)
	{
		this.service = service;
		injector.injectInstance(this);
		
		// Create all necessary models here
		model = Models.createPM(Contact.class, contact);
		
		// Following line is redundant with previous declaration of Contact mock, hence useless
		Contact of = model.of();
		firstName = model.getPropertyModel(of.getFirstName());
		lastName = model.getPropertyModel(of.getLastName());
		birth = model.getPropertyModel(of.getBirth());
		
		homeAddress = new AddressPM(model.getPropertyModel(of.getHome()));
		officeAddress = new AddressPM(model.getPropertyModel(of.getOffice()));
		picture = new PictureModel();
		
		title = new TitleConverter(model.getBeanChannel());
	}
	
	ValueModel<Contact> contactModel()
	{
		return model.getBeanChannel();
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
	
	//TODO Need to listen to changes in getBeanChannel and propagate changes to listeners
	// Strangely this class must be public or JGoodies Bindings will throw an exception...
	public class PictureModel extends AbstractValueModel<Icon>
	{
		PictureModel()
		{
		}
		
		@Override public Icon getValue()
		{
			Contact contact = model.getBeanChannel().getValue();
			if (contact != null)
			{
				return service.getContactPicture(contact.getId());
			}
			else
			{
				//TODO repalce with a default image that says "no image"
				return null;
			}
		}

		@Override public void setValue(Icon picture)
		{
			// Do nothing, current AddressBookService has no way to change contact picture
		}
	}
	
	final private AddressBookService service;
	final private GutsPresentationModel<Contact> model;
	final public ValueModel<String> firstName;
	final public ValueModel<String> lastName;
	final public ValueModel<Date> birth;
	final public AddressPM homeAddress;
	final public AddressPM officeAddress;
	final public ValueModel<Icon> picture;
	final public ValueModel<String> title;

	// Injected as resource
	private String titleWithContactFormat;
	private String titleWithoutContact;
}
