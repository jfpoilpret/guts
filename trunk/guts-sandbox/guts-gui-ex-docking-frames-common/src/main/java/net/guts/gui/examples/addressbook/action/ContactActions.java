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

import net.guts.event.Consumes;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.docking.ViewFactory;
import net.guts.gui.docking.ViewHelperService;
import net.guts.gui.examples.addressbook.business.AddressBookService;
import net.guts.gui.examples.addressbook.dialog.ContactDetailPanelController;
import net.guts.gui.examples.addressbook.docking.ViewHelper;
import net.guts.gui.examples.addressbook.docking.Views;
import net.guts.gui.examples.addressbook.domain.Contact;
import net.guts.gui.examples.addressbook.view.ContactPictureView;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.message.UserChoice;
import net.guts.gui.resource.ResourceInjector;
import net.guts.gui.task.AbstractTask;
import net.guts.gui.task.FeedbackController;
import net.guts.gui.task.Task;
import net.guts.gui.task.TaskInfo;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.blocker.InputBlockers;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.intern.AbstractCDockable;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContactActions
{
	static final public String OPEN_CONTACT_PICT_TOPIC = "OpenContactPicture";

	@Inject public ContactActions(AddressBookService service,
		ContactDetailPanelController detailController,
		MessageFactory messageFactory,
		ResourceInjector injector, 
		CControl controller,
		ViewFactory viewFactory, 
		ViewHelperService viewHelper)
	{
		_service = service;
		_detailController = detailController;
		_messageFactory = messageFactory;
		_controller = controller;
		_viewFactory = viewFactory;
		_viewHelper = viewHelper;
		setContactSelected(false);
		// Make sure resources (used for picture view tab) get injected
		injector.injectInstance(this);
	}
	
	@Consumes public void onContactSelectionChange(Contact selected)
	{
		_selected = selected;
		setContactSelected(selected != null);
		// Make sure to select the right picture view if it is open
		_viewHelper.selectView(ViewHelper.getContactPictureViewId(_selected));
	}
	
	@Consumes(topic = OPEN_CONTACT_PICT_TOPIC) 
	public void onContactPictureOpen(Contact selected)
	{
		_selected = selected;
		setContactSelected(selected != null);
		_openContactPicture.actionPerformed(null);
	}

	public void setContactSelected(boolean contactSelected)
	{
		_modifyContact.setEnabled(contactSelected);
		_deleteContact.setEnabled(contactSelected);
	}
	
	//TODO any possibility to refactor parts of this method into a general docking utility?
	// (in ViewHelperService for instance)
	private void showPictureView(Contact contact, Icon picture)
	{
		CWorkingArea area = 
			(CWorkingArea) _controller.getSingleDockable(Views.ContactPicture.name());
		area.setVisible(true);
		// Check that this picture is not already docked
		String idView = ViewHelper.getContactPictureViewId(contact);
		MultipleCDockable view = _controller.getMultipleDockable(idView);
		if (view == null)
		{
			// Build view
			ContactPictureView content = new ContactPictureView(picture);
			view = _viewFactory.createMulti(Views.ContactPicture.name(), idView, content);
			if (view instanceof DefaultMultipleCDockable)
			{
				DefaultMultipleCDockable viewImpl = (DefaultMultipleCDockable) view;
				// Set the right tabtext/icon
				viewImpl.setTitleIcon(_pictureViewIcon);
				String tabText = String.format(
					_pictureViewTabText, contact.getFirstName(), contact.getLastName());
				viewImpl.setTitleText(tabText);
			}
		}
		if (!view.isVisible())
		{
			view.setLocation(CLocation.working(area).stack());
			view.setVisible(true);
		}
		else
		{
			((AbstractCDockable) view).toFront();
		}
	}
	
	final public GutsAction _createContact = new GutsAction()
	{
		@Override protected void perform()
		{
			_detailController.showContactDialog(null);
		}
	};

	final public GutsAction _modifyContact = new GutsAction()
	{
		@Override protected void perform()
		{
			_detailController.showContactDialog(_selected);
		}
	};

	final public GutsAction _deleteContact = new TaskAction()
	{
		@Override protected void perform()
		{
			if (UserChoice.YES == _messageFactory.showMessage(
				"confirm-delete", _selected.getFirstName(), _selected.getLastName()))
			{
				final Contact contact = _selected;
				final Component parent = (Component) event().getSource();
				Task<Void> task = new AbstractTask<Void>()
				{
					@Override public Void execute(FeedbackController controller)
						throws InterruptedException
					{
						_service.removeContact(contact);
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
			// First check if this picture already has an open view
			if (!_viewHelper.selectView(ViewHelper.getContactPictureViewId(_selected)))
			{
				final Contact contact = _selected;
				Task<Icon> task = new AbstractTask<Icon>()
				{
					@Override public Icon execute(FeedbackController controller) 
						throws InterruptedException
					{
						return _service.getContactPicture(contact.getId());
					}
	
					@Override public void succeeded(
						TasksGroup group, TaskInfo source, Icon picture)
					{
						showPictureView(contact, picture);
					}
				};
				submit(task, InputBlockers.actionBlocker(this));
			}
		}
	};

	final private MessageFactory _messageFactory;
	final private AddressBookService _service;
	final private ContactDetailPanelController _detailController;
	final private CControl _controller;
	final private ViewFactory _viewFactory;
	final private ViewHelperService _viewHelper;
	private Contact _selected = null;
	// The following fields are injected resources
	private Icon _pictureViewIcon;
	private String _pictureViewTabText;
}
