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
		_model = Models.createPM(Contact.class, contact);
		
		// Following line is redundant with previous declaration of Contact mock, hence useless
		Contact of = _model.of();
		_firstName = _model.getPropertyModel(of.getFirstName());
		_lastName = _model.getPropertyModel(of.getLastName());
		_birth = _model.getPropertyModel(of.getBirth());
		
		_homeAddress = new AddressPM(_model.getPropertyModel(of.getHome()));
		_officeAddress = new AddressPM(_model.getPropertyModel(of.getOffice()));
		
		_title = new TitleConverter(_model.getBeanChannel());
	}
	
	ValueModel<Contact> contactModel()
	{
		return _model.getBeanChannel();
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
	
	final private GutsPresentationModel<Contact> _model;
	final public ValueModel<String> _firstName;
	final public ValueModel<String> _lastName;
	final public ValueModel<Date> _birth;
	final public AddressPM _homeAddress;
	final public AddressPM _officeAddress;
	final public ValueModel<String> _title;
}
