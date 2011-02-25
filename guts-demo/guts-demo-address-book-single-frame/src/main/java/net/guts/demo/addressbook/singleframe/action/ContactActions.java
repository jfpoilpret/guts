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

package net.guts.demo.addressbook.singleframe.action;

import java.awt.Component;

import javax.swing.JComponent;

import net.guts.demo.addressbook.singleframe.business.AddressBookService;
import net.guts.demo.addressbook.singleframe.dialog.ContactDetailPanel;
import net.guts.demo.addressbook.singleframe.dialog.ContactDetailTabPanel;
import net.guts.demo.addressbook.singleframe.dialog.ContactStep;
import net.guts.demo.addressbook.singleframe.dialog.HomeAddressStep;
import net.guts.demo.addressbook.singleframe.dialog.OfficeAddressStep;
import net.guts.demo.addressbook.singleframe.domain.Contact;
import net.guts.event.Consumes;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.message.UserChoice;
import net.guts.gui.task.AbstractTask;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.gui.task.TaskInfo;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.blocker.InputBlockers;
import net.guts.gui.template.okcancel.OkCancel;
import net.guts.gui.template.wizard.Wizard;
import net.guts.gui.window.JDialogConfig;

import com.google.inject.Inject;
import com.google.inject.Provider;
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
		_modifyContact.setEnabled(contactSelected);
		_modifyContactWithTabs.setEnabled(contactSelected);
		_modifyContactWithWizard.setEnabled(contactSelected);
		_deleteContact.setEnabled(contactSelected);
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

	private void showContactWithOneDialog(Contact contact)
	{
		final boolean isNew = (contact == null);
		final ContactDetailPanel panel = _contactDetail.get();
		panel.modelToView(contact);
		OkCancel template = OkCancel.create().withCancel().withOK(new GutsAction()
		{
			@Override protected void perform()
			{
				if (isNew)
				{
					_service.createContact(panel.viewToModel());
				}
				else
				{
					_service.modifyContact(panel.viewToModel());
				}
			}
		});
		_dialogFactory.showDialog(panel, JDialogConfig.create().merge(template).config());
	}
	
	final private GutsAction _createContact = new GutsAction()
	{
		@Override protected void perform()
		{
			showContactWithOneDialog(null);
		}
	};

	final private GutsAction _modifyContact = new GutsAction()
	{
		@Override protected void perform()
		{
			showContactWithOneDialog(_selected);
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

	private void showContactWithTabs(Contact contact)
	{
		final boolean isNew = (contact == null);
		final ContactDetailTabPanel panel = _tabContactDetail.get();
		panel.modelToView(contact);
		OkCancel template = OkCancel.create().withCancel().withOK(new GutsAction()
		{
			@Override protected void perform()
			{
				if (isNew)
				{
					_service.createContact(panel.viewToModel());
				}
				else
				{
					_service.modifyContact(panel.viewToModel());
				}
			}
		});
		_dialogFactory.showDialog(panel, JDialogConfig.create().merge(template).config());
	}
	
	final private GutsAction _createContactWithTabs = new GutsAction()
	{
		@Override protected void perform()
		{
			showContactWithTabs(null);
		}
	};

	final private GutsAction _modifyContactWithTabs = new GutsAction()
	{
		@Override protected void perform()
		{
			showContactWithTabs(_selected);
		}
	};
	
	private void showContactWithWizard(final Contact contact, final boolean isNew)
	{
		// Instantiate each step panel, setup their models
		_contactStep.modelToView(contact);
		_homeAddressStep.modelToView(contact);
		_officeAddressStep.modelToView(contact);
		// Prepare wizard config
		Wizard wizard = Wizard.create()
			.mapNextStep(_contactStep)
			.mapNextStep(_homeAddressStep)
			.mapNextStep(_officeAddressStep)
			.withFinish(new GutsAction()
			{
				@Override protected void perform()
				{
					_contactStep.accept();
					_homeAddressStep.accept();
					_officeAddressStep.accept();
					if (isNew)
					{
						_service.createContact(contact);
					}
					else
					{
						_service.modifyContact(contact);
					}
				}
			});
		JComponent mainView = wizard.mainView();
		mainView.setName("ContactDetailWizardPanel");
		_dialogFactory.showDialog(mainView, JDialogConfig.create().merge(wizard).config());
	}

	final private GutsAction _createContactWithWizard = new GutsAction()
	{
		@Override protected void perform()
		{
			showContactWithWizard(new Contact(), true);
		}
	};

	final private GutsAction _modifyContactWithWizard = new GutsAction()
	{
		@Override protected void perform()
		{
			showContactWithWizard(_selected, false);
		}
	};

	// Services
	@Inject private DialogFactory _dialogFactory;
	@Inject private MessageFactory _messageFactory;
	@Inject private AddressBookService _service;
	
	// Panel providers
	@Inject Provider<ContactDetailPanel> _contactDetail;
	@Inject Provider<ContactDetailTabPanel> _tabContactDetail;
	@Inject ContactStep _contactStep;
	@Inject HomeAddressStep _homeAddressStep;
	@Inject OfficeAddressStep _officeAddressStep;
	
	private Contact _selected = null;
}
