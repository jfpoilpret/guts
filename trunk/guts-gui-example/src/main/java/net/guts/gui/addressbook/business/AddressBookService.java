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

package net.guts.gui.addressbook.business;

import java.util.List;

import javax.swing.Icon;

import net.guts.gui.addressbook.domain.Contact;

import com.google.inject.ImplementedBy;

@ImplementedBy(AddressBookServiceImpl.class)
public interface AddressBookService
{
	public List<Contact> getAllContacts();
	public List<Contact> searchContacts(Contact criteria);
	public void createContact(Contact contact);
	public void modifyContact(Contact contact);
	public void removeContact(Contact contact);
	public Icon getContactPicture(int id);
}
