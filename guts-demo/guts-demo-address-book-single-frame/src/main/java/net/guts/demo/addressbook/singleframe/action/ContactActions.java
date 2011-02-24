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

	final private GutsAction _createContact = new GutsAction()
	{
		@Override protected void perform()
		{
			final ContactDetailPanel panel = _contactDetail.get();
			panel.modelToView(null);
			OkCancel template = OkCancel.create().withCancel().withOK(new GutsAction()
			{
				@Override protected void perform()
				{
					_service.createContact(panel.viewToModel());
				}
			});
			_dialogFactory.showDialog(panel, JDialogConfig.create().merge(template).config());
		}
	};

	final private GutsAction _modifyContact = new GutsAction()
	{
		@Override protected void perform()
		{
			final ContactDetailPanel panel = _contactDetail.get();
			panel.modelToView(_selected);
			OkCancel template = OkCancel.create().withCancel().withOK(new GutsAction()
			{
				@Override protected void perform()
				{
					_service.modifyContact(panel.viewToModel());
				}
			});
			_dialogFactory.showDialog(panel, JDialogConfig.create().merge(template).config());
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
			final ContactDetailTabPanel panel = _tabContactDetail.get();
			panel.modelToView(null);
			OkCancel template = OkCancel.create().withCancel().withOK(new GutsAction()
			{
				@Override protected void perform()
				{
					_service.createContact(panel.viewToModel());
				}
			});
			_dialogFactory.showDialog(panel, JDialogConfig.create().merge(template).config());
		}
	};

	final private GutsAction _modifyContactWithTabs = new GutsAction()
	{
		@Override protected void perform()
		{
			final ContactDetailTabPanel panel = _tabContactDetail.get();
			panel.modelToView(_selected);
			OkCancel template = OkCancel.create().withCancel().withOK(new GutsAction()
			{
				@Override protected void perform()
				{
					_service.modifyContact(panel.viewToModel());
				}
			});
			_dialogFactory.showDialog(panel, JDialogConfig.create().merge(template).config());
		}
	};

	final private GutsAction _createContactWithWizard = new GutsAction()
	{
		@Override protected void perform()
		{
			// Instantiate each step panel, setup their models
			final Contact contact = new Contact();
			final ContactStep contactStep = _contactStep.get();
			final HomeAddressStep homeAddressStep = _homeAddressStep.get();
			final OfficeAddressStep officeAddressStep = _officeAddressStep.get();
			contactStep.modelToView(contact);
			homeAddressStep.modelToView(contact);
			officeAddressStep.modelToView(contact);
			// Prepare wizard config
			Wizard wizard = Wizard.create()
				.mapNextStep(contactStep)
				.mapNextStep(homeAddressStep)
				.mapNextStep(officeAddressStep)
				.withFinish(new GutsAction()
				{
					@Override protected void perform()
					{
						contactStep.accept();
						homeAddressStep.accept();
						officeAddressStep.accept();
						_service.createContact(contact);
					}
				});
			JComponent mainView = wizard.mainView();
			mainView.setName("ContactDetailWizardPanel");
			_dialogFactory.showDialog(mainView, JDialogConfig.create().merge(wizard).config());
		}
	};

	final private GutsAction _modifyContactWithWizard = new GutsAction()
	{
		@Override protected void perform()
		{
			// Instantiate each step panel, setup their models
			final Contact contact = _selected;
			final ContactStep contactStep = _contactStep.get();
			final HomeAddressStep homeAddressStep = _homeAddressStep.get();
			final OfficeAddressStep officeAddressStep = _officeAddressStep.get();
			contactStep.modelToView(contact);
			homeAddressStep.modelToView(contact);
			officeAddressStep.modelToView(contact);
			// Prepare wizard config
			Wizard wizard = Wizard.create()
				.mapNextStep(contactStep)
				.mapNextStep(homeAddressStep)
				.mapNextStep(officeAddressStep)
				.withFinish(new GutsAction()
				{
					@Override protected void perform()
					{
						contactStep.accept();
						homeAddressStep.accept();
						officeAddressStep.accept();
						_service.modifyContact(contact);
					}
				});
			JComponent mainView = wizard.mainView();
			mainView.setName("ContactDetailWizardPanel");
			_dialogFactory.showDialog(mainView, JDialogConfig.create().merge(wizard).config());
		}
	};

	// Services
	@Inject private DialogFactory _dialogFactory;
	@Inject private MessageFactory _messageFactory;
	@Inject private AddressBookService _service;
	
	// Panel providers
	@Inject Provider<ContactDetailPanel> _contactDetail;
	@Inject Provider<ContactDetailTabPanel> _tabContactDetail;
	@Inject Provider<ContactStep> _contactStep;
	@Inject Provider<HomeAddressStep> _homeAddressStep;
	@Inject Provider<OfficeAddressStep> _officeAddressStep;
	
	private Contact _selected = null;
}
