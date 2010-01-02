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

import net.guts.event.Consumes;
import net.guts.gui.action.AbstractTask;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.InputBlockers;
import net.guts.gui.action.Task;
import net.guts.gui.action.TaskController;
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
public class ContactActions
{
	public ContactActions()
	{
		setContactSelected(false);
	}
	
	@Consumes public void onContactSelectionChange(Contact selected)
	{
		_selected = selected;
		setContactSelected(selected != null);
	}
	
	public void setContactSelected(boolean contactSelected)
	{
		_modifyContact.action().setEnabled(contactSelected);
		_modifyContactWithTabs.action().setEnabled(contactSelected);
		_modifyContactWithWizard.action().setEnabled(contactSelected);
		_deleteContact.action().setEnabled(contactSelected);
	}
	
	public GutsAction createContact()
	{
		return _createContact;
	}
	
	public GutsAction createContactWithTabs()
	{
		return _createContactWithTabs;
	}
	
	public GutsAction createContactWithWizard()
	{
		return _createContactWithWizard;
	}
	
	public GutsAction modifyContact()
	{
		return _modifyContact;
	}
	
	public GutsAction modifyContactWithTabs()
	{
		return _modifyContactWithTabs;
	}
	
	public GutsAction modifyContactWithWizard()
	{
		return _modifyContactWithWizard;
	}
	
	public GutsAction deleteContact()
	{
		return _deleteContact;
	}

	final private GutsAction _createContact = new GutsAction("createContact")
	{
		@Override protected void perform()
		{
			_dialogFactory.showDialog(ContactPanel.class);
		}
	};

	final private GutsAction _modifyContact = new GutsAction("modifyContact")
	{
		@Override protected void perform()
		{
			_dialogFactory.showDialog(
				ContactPanel.class, new ComponentInitializer<ContactPanel>()
			{
				public void init(ContactPanel panel)
				{
					panel.setContact(_selected);
				}
			});
		}
	};

	//TODO tests of guts-gui Task behavior here!
	final private GutsAction _deleteContact = new GutsAction("deleteContact")
	{
		@Override protected void perform()
		{
			if (UserChoice.YES == _messageFactory.showMessage(
				"confirm-delete", _selected.getFirstName(), _selected.getLastName()))
			{
				Task<Void, Void> task = new AbstractTask<Void, Void>()
				{
					@Override public Void doInBackground(TaskController<Void> publisher)
						throws InterruptedException
					{
						Thread.sleep(5000L);
						_service.removeContact(_selected);
						return null;
					}

					@Override public void succeeded(Task<?, ?> source, Void result)
					{
						_messageFactory.showMessage("delete-done");
					}
				};
				getDefaultTaskService().execute(task, InputBlockers.createActionBlocker(this));
			}
		}
	};
	
	final private GutsAction _createContactWithTabs = new GutsAction("createContactWithTabs")
	{
		@Override protected void perform()
		{
			_dialogFactory.showDialog(ContactTabPanel.class);
		}
	};

	final private GutsAction _modifyContactWithTabs = new GutsAction("modifyContactWithTabs")
	{
		@Override protected void perform()
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
	};

	final private GutsAction _createContactWithWizard = 
		new GutsAction("createContactWithWizard")
	{
		@Override protected void perform()
		{
			_dialogFactory.showDialog(ContactWizardPanel.class);
		}
	};

	final private GutsAction _modifyContactWithWizard = 
		new GutsAction("modifyContactWithWizard")
	{
		@Override protected void perform()
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
	};

	@Inject private DialogFactory _dialogFactory;
	@Inject private MessageFactory _messageFactory;
	@Inject private AddressBookService _service;
	private Contact _selected = null;
}
