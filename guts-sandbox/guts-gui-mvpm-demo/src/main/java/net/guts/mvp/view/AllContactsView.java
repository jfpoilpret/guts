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

package net.guts.mvp.view;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

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
import net.guts.mvp.presenter.ContactPM;
import net.guts.mvp.presenter.AllContactsPM;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.jgoodies.binding.list.SelectionInList;

@Singleton
public class AllContactsView extends JPanel
{
	static final private long serialVersionUID = 7068262166438989381L;

	@Inject public AllContactsView(AllContactsPM model, 
		ContactViewFactory contactViewFactory,
		DialogFactory dialogFactory, MessageFactory messageFactory)
	{
		this.dialogFactory = dialogFactory;
		this.messageFactory = messageFactory;
		this.contactViewFactory = contactViewFactory;
		this.model = model;
		selection = model.contacts;
		
		// Initialize components
		txfFirstName.setEditable(false);
		txfLastName.setEditable(false);
		txfBirth.setEditable(false);
		table.setModel(model.contactsTableModel);

		// Setup components bindings
		GutsBindings.bind(table, selection);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GutsBindings.bind(txfFirstName, model.selection.firstName);
		GutsBindings.bind(txfLastName, model.selection.lastName);
		GutsBindings.bind(txfBirth, model.selection.birth);
		GutsBindings.bind(txaAddress, model.selection.homeAddress.compactAddress);
		GutsBindings.connectTitle(this, model.selection.title);

		//TODO move out of the View!
		// Connect actions to existence of selection
		GutsBindings.connectActionsEnableToSelection(selection, delete, modify);
		GutsBindings.bindDoubleClick(table, modify);
		GutsBindings.bindEnter(table, modify);
		
		// Layout the form
		//TODO add more info (address)
		DesignGridLayout layout = new DesignGridLayout(this);
		layout.withoutConsistentWidthAcrossNonGridRows();
		layout.row().center().add(new JScrollPane(table));
		layout.emptyRow();
		layout.row().grid(lblFirstName).add(txfFirstName);
		layout.row().grid(lblLastName).add(txfLastName);
		layout.row().grid(lblBirth).add(txfBirth);
		layout.row().grid(lblAddress).add(txaAddress);
		layout.emptyRow();
		layout.row().right().add(btnCreate, btnModify, btnDelete);
	}
	
	//TODO move all actions out of View!???
	
	final private GutsAction create = new GutsAction()
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
	
	final private GutsAction modify = new GutsAction()
	{
		@Override protected void perform()
		{
			OkCancel template = OkCancel.create().withOK(model._saveSelectedContact).withCancel();
			JDialogConfig config = JDialogConfig.create().merge(template);
			ContactView view = contactViewFactory.create(model.selection);
			dialogFactory.showDialog(view, config.config());
		}
	};
	
	final private GutsAction delete = new TaskAction()
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
	
	final private JTable table = new JTable();
	final private JLabel lblFirstName = new JLabel();
	final private JTextField txfFirstName = new JTextField();
	final private JLabel lblLastName = new JLabel();
	final private JTextField txfLastName = new JTextField();
	final private JLabel lblBirth = new JLabel();
	final private JFormattedTextField txfBirth = new JFormattedTextField();
	final private JLabel lblAddress = new JLabel();
	final private JTextArea txaAddress = new JTextArea(4, 40);
	final private JButton btnModify = new JButton(modify);
	final private JButton btnCreate = new JButton(create);
	final private JButton btnDelete = new JButton(delete);
}
