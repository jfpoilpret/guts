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

package net.guts.gui.addressbook.action;

import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.Action;

import net.guts.event.Consumes;
import net.guts.gui.addressbook.business.AddressBookService;
import net.guts.gui.addressbook.dialog.ContactPanel;
import net.guts.gui.addressbook.dialog.ContactTabPanel;
import net.guts.gui.addressbook.dialog.ContactWizardPanel;
import net.guts.gui.addressbook.domain.Contact;
import net.guts.gui.dialog.ComponentInitializer;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.message.UserChoice;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContactActions extends AbstractBean 
{
	static private final String CONTACT_SELECTED = "contactSelected";
	
	@Consumes public void onContactSelectionChange(Contact selected)
	{
		_selected = selected;
		setContactSelected(selected != null);
	}
	
	public boolean isContactSelected()
	{
		return _contactSelected;
	}

	public void setContactSelected(boolean contactSelected)
	{
		boolean old = _contactSelected;
		_contactSelected = contactSelected;
		firePropertyChange(CONTACT_SELECTED, old, _contactSelected);
	}

	@Action
	public void createContact()
	{
		_dialogFactory.showDialog(ContactPanel.class);
	}

	@Action(enabledProperty = CONTACT_SELECTED)
	public void modifyContact()
	{
		_dialogFactory.showDialog(ContactPanel.class, new ComponentInitializer<ContactPanel>()
		{
			public void init(ContactPanel panel)
			{
				panel.setContact(_selected);
			}
		});
	}

	@Action(enabledProperty = CONTACT_SELECTED)
	public void deleteContact()
	{
		if (UserChoice.YES == _messageFactory.showMessage(
			"confirm-delete", _selected.getFirstName(), _selected.getLastName()))
		{
			_service.removeContact(_selected);
		}
	}
	
	@Action
	public void createContactWithTabs()
	{
		_dialogFactory.showDialog(ContactTabPanel.class);
	}

	@Action(enabledProperty = CONTACT_SELECTED)
	public void modifyContactWithTabs()
	{
		_dialogFactory.showDialog(
			ContactTabPanel.class, new ComponentInitializer<ContactTabPanel>()
		{
			public void init(ContactTabPanel panel)
			{
				panel.setContact(_selected);
			}
		});
	}

	@Action
	public void createContactWithWizard()
	{
		_dialogFactory.showDialog(ContactWizardPanel.class);
	}

	@Action(enabledProperty = CONTACT_SELECTED)
	public void modifyContactWithWizard()
	{
		_dialogFactory.showDialog(
			ContactWizardPanel.class, new ComponentInitializer<ContactWizardPanel>()
		{
			public void init(ContactWizardPanel panel)
			{
				panel.setContact(_selected);
			}
		});
	}

	@Inject private DialogFactory _dialogFactory;
	@Inject private MessageFactory _messageFactory;
	@Inject private AddressBookService _service;
	private Contact _selected = null;
	private boolean _contactSelected = false;
}
