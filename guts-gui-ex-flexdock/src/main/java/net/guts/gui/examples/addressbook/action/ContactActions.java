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

import javax.swing.Icon;
import javax.swing.JComponent;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

import net.guts.event.Consumes;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.application.WindowController.StatePolicy;
import net.guts.gui.application.docking.DockingHelper;
import net.guts.gui.application.docking.ViewFactory;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.dialog.PanelInitializer;
import net.guts.gui.examples.addressbook.business.AddressBookService;
import net.guts.gui.examples.addressbook.dialog.ContactDetailPanel;
import net.guts.gui.examples.addressbook.dialog.ContactDetailTabPanel;
import net.guts.gui.examples.addressbook.dialog.ContactDetailWizardPanel;
import net.guts.gui.examples.addressbook.docking.ViewHelper;
import net.guts.gui.examples.addressbook.docking.Views;
import net.guts.gui.examples.addressbook.domain.Contact;
import net.guts.gui.examples.addressbook.view.ContactPictureView;
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
	static final public String OPEN_CONTACT_PICT_TOPIC = "OpenContactPicture";

	@Inject public ContactActions(AddressBookService service,
		DialogFactory dialogFactory, MessageFactory messageFactory,
		ViewFactory viewFactory)
	{
		_service = service;
		_dialogFactory = dialogFactory;
		_messageFactory = messageFactory;
		_viewFactory = viewFactory;
		setContactSelected(false);
	}
	
	@Consumes public void onContactSelectionChange(Contact selected)
	{
		_selected = selected;
		setContactSelected(selected != null);
	}
	
	@Consumes(topic = OPEN_CONTACT_PICT_TOPIC) 
	public void onContactPictureOpen(Contact selected)
	{
		_selected = selected;
		setContactSelected(selected != null);
		_openContactPicture.action().actionPerformed(null);
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
		Class<T> clazz, PanelInitializer<T> initializer)
	{
		_dialogFactory.showDialog(
			clazz, BoundsPolicy.PACK_AND_CENTER, StatePolicy.RESTORE_IF_EXISTS, initializer);
	}

	private <T extends JComponent> void showDialog(Class<T> clazz)
	{
		_dialogFactory.showDialog(
			clazz, BoundsPolicy.PACK_AND_CENTER, StatePolicy.RESTORE_IF_EXISTS);
	}

	static private Viewport findPictureViewport()
	{
		return DockingHelper.findEmptyableViewport(Views.ContactPicture.name());
	}
	
	final private GutsAction _createContact = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactDetailPanel.class);
		}
	};

	final private GutsAction _modifyContact = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactDetailPanel.class, new PanelInitializer<ContactDetailPanel>()
			{
				public void init(ContactDetailPanel panel)
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
	
	final private GutsAction _openContactPicture = new TaskAction()
	{
		@Override protected void perform()
		{
			final Contact contact = _selected;
			Task<Icon> task = new AbstractTask<Icon>()
			{
				@Override public Icon execute(FeedbackController controller) 
					throws InterruptedException
				{
					return _service.getContactPicture(contact.getId());
				}

				//TODO should be more complex than that in fact...
				// reopen view if already exists, rather than re-create
				@Override public void succeeded(
					TasksGroup group, TaskInfo source, Icon picture)
				{
					// Find the Viewport onto which pictures must be displayed
					Viewport port = findPictureViewport();
					ContactPictureView content = new ContactPictureView(picture);
					// Build view
					String idView = ViewHelper.getContactPictureViewId(contact);
					View view = _viewFactory.createView(idView, content);
					// Dock view at the right place
					DockingManager.dock((Dockable) view, port, DockingConstants.CENTER_REGION);
				}
			};
			submit(task, InputBlockers.actionBlocker(this));
		}
	};

	final private GutsAction _createContactWithTabs = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactDetailTabPanel.class);
		}
	};

	final private GutsAction _modifyContactWithTabs = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactDetailTabPanel.class, 
				new PanelInitializer<ContactDetailTabPanel>()
			{
				public void init(ContactDetailTabPanel panel)
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
			showDialog(ContactDetailWizardPanel.class);
		}
	};

	final private GutsAction _modifyContactWithWizard = new GutsAction()
	{
		@Override protected void perform()
		{
			showDialog(ContactDetailWizardPanel.class, 
				new PanelInitializer<ContactDetailWizardPanel>()
			{
				public void init(ContactDetailWizardPanel panel)
				{
					panel.setContact(_selected);
				}
			});
		}
	};

	final private DialogFactory _dialogFactory;
	final private MessageFactory _messageFactory;
	final private ViewFactory _viewFactory;
	final private AddressBookService _service;
	private Contact _selected = null;
}
