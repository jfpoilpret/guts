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

package net.guts.mvp.action;

import net.guts.binding.GutsBindings;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.message.UserChoice;
import net.guts.gui.task.blocker.InputBlockers;
import net.guts.gui.template.okcancel.OkCancel;
import net.guts.gui.window.JDialogConfig;
import net.guts.mvp.domain.Contact;
import net.guts.mvp.presenter.AllContactsPM;
import net.guts.mvp.presenter.ContactPM;
import net.guts.mvp.view.ContactView;
import net.guts.mvp.view.ContactViewFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jgoodies.binding.list.SelectionInList;

@Singleton
public class AllContactsUiActions
{	
	@Inject AllContactsUiActions(AllContactsPM model, 
		ContactViewFactory contactViewFactory,
		DialogFactory dialogFactory, MessageFactory messageFactory)
	{
		this.dialogFactory = dialogFactory;
		this.messageFactory = messageFactory;
		this.contactViewFactory = contactViewFactory;
		this.model = model;
		selection = model.contacts;

		// Connect actions to existence of selection
		GutsBindings.connectActionsEnableToSelection(selection, delete, modify);
	}

	final public GutsAction create = new GutsAction()
	{
		@Override protected void perform()
		{
			// ask PM to create new ContactPM
			ContactPM contactModel = model.createContact();
			OkCancel template = OkCancel.create().withOK(model.saveContact(contactModel)).withCancel();
			JDialogConfig config = JDialogConfig.create().merge(template);
			ContactView view = contactViewFactory.create(contactModel);
			dialogFactory.showDialog(view, config.config());
		}
	};
	
	final public GutsAction modify = new GutsAction()
	{
		@Override protected void perform()
		{
			OkCancel template = OkCancel.create().withOK(model.saveSelectedContact).withCancel();
			JDialogConfig config = JDialogConfig.create().merge(template);
			ContactView view = contactViewFactory.create(model.selection);
			dialogFactory.showDialog(view, config.config());
		}
	};
	
	final public GutsAction delete = new TaskAction()
	{
		@Override protected void perform()
		{
			// First ask user confirmation
			Contact selected = selection.getSelection();
			if (UserChoice.YES == messageFactory.showMessage(
				"confirm-delete", selected.getFirstName(), selected.getLastName()))
			{
				submit(model.getDeleteTask(), InputBlockers.actionBlocker(this));
			}
		}
	};
	
	final private DialogFactory dialogFactory;
	final private MessageFactory messageFactory;
	final private ContactViewFactory contactViewFactory;

	final private AllContactsPM model;
	final private SelectionInList<Contact> selection;
}
