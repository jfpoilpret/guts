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

package net.guts.demo.addressbook.singleframe.business;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.guts.demo.addressbook.singleframe.domain.Address;
import net.guts.demo.addressbook.singleframe.domain.Contact;

import com.google.inject.Singleton;

@Singleton
public class AddressBookServiceImpl implements AddressBookService
{
	public AddressBookServiceImpl()
	{
		// Hard-code a few contacts
		_contacts.add(create("John", "Smith", "24.09.1966", null));
		_contacts.add(create("Phoebe", "Smith", "04.04.1978", null));
		_contacts.add(create("Luke", "Smith", "04.06.2003", null));
		_contacts.add(create("Linda", "Doe", "27.10.1960", "San Francisco"));
		_contacts.add(create("Mary", "Doe", "04.05.1964", "New York"));
	}

	@Override public void createContact(Contact contact)
	{
		contact.setId(++_next);
		_contacts.add(contact);
	}

	@Override public List<Contact> getAllContacts()
	{
		return _contacts;
	}

	@Override public void modifyContact(Contact contact)
	{
		_contacts.set(findById(contact.getId()), contact);
	}

	@Override public void removeContact(Contact contact)
	{
		_contacts.remove(findById(contact.getId()));
	}

	@Override public Icon getContactPicture(int id)
    {
		String path = "/contacts/" + id + ".jpg";
		URL url = getClass().getResource(path);
		if (url == null)
		{
			return null;
		}
		Icon icon = new ImageIcon(url);
		return icon;
    }

	@Override public List<Contact> searchContacts(Contact criteria)
	{
		List<Contact> results = new ArrayList<Contact>();
		for (Contact contact: _contacts)
		{
			if (match(contact, criteria))
			{
				results.add(contact);
			}
		}
		return results;
	}
	
	// CSOFF: ReturnCount
	// CSOFF: NeedBraces
	static protected boolean match(Contact source, Contact criteria)
	{
		if (!check(source.getFirstName(), criteria.getFirstName()))
			return false;
		if (!check(source.getLastName(), criteria.getLastName()))
			return false;
		if (!check(source.getBirth(), criteria.getBirth()))
			return false;
		if (!check(source.getHome(), criteria.getHome()))
			return false;
		if (!check(source.getOffice(), criteria.getOffice()))
			return false;
		return true;
	}
	// CSON: NeedBraces
	// CSON: ReturnCount
	
	// CSOFF: NPathComplexity
	// CSOFF: ReturnCount
	// CSOFF: NeedBraces
	static protected boolean check(Address source, Address criterion)
	{
		if (criterion == null)
			return true;
		if (source == null)
			return false;
		if (!check(source.getStreet1(), criterion.getStreet1()))
			return false;
		if (!check(source.getStreet2(), criterion.getStreet2()))
			return false;
		if (!check(source.getZip(), criterion.getZip()))
			return false;
		if (!check(source.getCity(), criterion.getCity()))
			return false;
		if (!check(source.getCountry(), criterion.getCountry()))
			return false;
		if (!check(source.getPhone(), criterion.getPhone()))
			return false;
		return true;
	}
	// CSON: NeedBraces
	// CSON: ReturnCount
	// CSON: NPathComplexity

	static protected boolean check(String source, String criterion)
	{
		return (criterion == null) || criterion.equalsIgnoreCase(source);
	}
	
	static protected boolean check(Object source, Object criterion)
	{
		return (criterion == null) || criterion.equals(source);
	}
	
	protected int findById(int id)
	{
		int index = 0;
		for (Contact contact: _contacts)
		{
			if (contact.getId() == id)
			{
				return index;
			}
			index++;
		}
		return -1;
	}
	
	protected Contact create(String firstName, String lastName, String birth, String city)
	{
		Date birthDate;
		try
		{
			birthDate = _format.parse(birth);
		}
		catch (ParseException e)
		{
			birthDate = null;
		}
		
		Contact contact = new Contact();
		contact.setId(++_next);
		contact.setFirstName(firstName);
		contact.setLastName(lastName);
		contact.setBirth(birthDate);
		contact.getHome().setCity(city);
		return contact;
	}

	final private List<Contact> _contacts = new ArrayList<Contact>();
	private int _next = 0;
	static final private DateFormat _format = new SimpleDateFormat("dd.MM.yyyy");
}
