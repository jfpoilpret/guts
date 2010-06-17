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

package net.guts.gui.examples.addressbook.action;

import java.awt.Component;

import javax.swing.JComponent;

import net.guts.event.Consumes;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.application.WindowController.StatePolicy;
import net.guts.gui.dialog.ComponentInitializer;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.examples.addressbook.business.AddressBookService;
import net.guts.gui.examples.addressbook.dialog.ContactPanel;
import net.guts.gui.examples.addressbook.dialog.ContactTabPanel;
import net.guts.gui.examples.addressbook.dialog.ContactWizardPanel;
import net.guts.gui.examples.addressbook.domain.Contact;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.message.UserChoice;
import net.guts.gui.task.AbstractTask;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.gui.task.TaskInfo;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.blocker.InputBlockers;

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

	// GUTS-22: showDialog convenience method to avoid repeats
	private <T extends JComponent> void showDialog(
		Class<T> clazz, ComponentInitializer<T> initializer)
	{
		_dialogFactory.showDialog(
			clazz, BoundsPolicy.PACK_AND_CENTER, StatePolicy.RESTORE_IF_EXISTS, initializer);
	}

	private <T extends JComponent> void showDialog(Class<T> clazz)
	{
		_dialogFactory.showDialog(
			clazz, BoundsPolicy.PACK_AND_CENTER, StatePolicy.RESTORE_IF_EXISTS);
	}

	final private GutsAction _createContact = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactPanel.class);
		}
	};

	final private GutsAction _modifyContact = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactPanel.class, new ComponentInitializer<ContactPanel>()
			{
				public void init(ContactPanel panel)
				{
					panel.setContact(_selected);
				}
			});
		}
	};

	final private GutsAction _deleteContact = new TaskAction()
	{
		@Override protected void perform()
		{
			if (UserChoice.YES == _messageFactory.showMessage(
				"confirm-delete", _selected.getFirstName(), _selected.getLastName()))
			{
				final Component parent = (Component) event().getSource();
				Task<Void> task = new AbstractTask<Void>()
				{
					@Override public Void execute(FeedbackController controller)
						throws InterruptedException
					{
						_service.removeContact(_selected);
						return null;
					}

					@Override public void succeeded(
						TasksGroup group, TaskInfo source, Void result)
					{
						_messageFactory.showMessage(parent, "delete-done");
					}
				};
				submit(task, InputBlockers.actionBlocker(this));
			}
		}
	};
	
	final private GutsAction _createContactWithTabs = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactTabPanel.class);
		}
	};

	final private GutsAction _modifyContactWithTabs = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactTabPanel.class, new ComponentInitializer<ContactTabPanel>()
			{
				public void init(ContactTabPanel panel)
				{
					panel.setContact(_selected);
				}
			});
		}
	};

	final private GutsAction _createContactWithWizard = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactWizardPanel.class);
		}
	};

	final private GutsAction _modifyContactWithWizard = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactWizardPanel.class, new ComponentInitializer<ContactWizardPanel>()
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
