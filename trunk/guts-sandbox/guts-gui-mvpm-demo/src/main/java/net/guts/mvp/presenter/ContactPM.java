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

import net.guts.binding.GutsPresentationModel;
import net.guts.binding.Models;
import net.guts.mvp.domain.Contact;

import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;

public class ContactPM
{
	ContactPM(ValueModel<Contact> contact)
	{
		// Create all necessary models here
		model = Models.createPM(Contact.class, contact);
		
		// Following line is redundant with previous declaration of Contact mock, hence useless
		Contact of = model.of();
		firstName = model.getPropertyModel(of.getFirstName());
		lastName = model.getPropertyModel(of.getLastName());
		birth = model.getPropertyModel(of.getBirth());
		
		homeAddress = new AddressPM(model.getPropertyModel(of.getHome()));
		officeAddress = new AddressPM(model.getPropertyModel(of.getOffice()));
		
		title = new TitleConverter(model.getBeanChannel());
	}
	
	ValueModel<Contact> contactModel()
	{
		return model.getBeanChannel();
	}

	//TODO does it have to be public???
	@SuppressWarnings("serial") 
	static public class TitleConverter extends AbstractConverter<Contact, String>
	{
		public TitleConverter(ValueModel<Contact> subject)
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
					TITLE_WITH_CONTACT_FORMAT, contact.getFirstName(), contact.getLastName());
			}
			else
			{
				return TITLE_WITHOUT_CONTACT;
			}
		}
		
		//TODO inject as resource
		static final private String TITLE_WITH_CONTACT_FORMAT = "Contact: `%s %s`";
		static final private String TITLE_WITHOUT_CONTACT = "Contacts List";
	}
	
	final private GutsPresentationModel<Contact> model;
	final public ValueModel<String> firstName;
	final public ValueModel<String> lastName;
	final public ValueModel<Date> birth;
	final public AddressPM homeAddress;
	final public AddressPM officeAddress;
	final public ValueModel<String> title;
}
