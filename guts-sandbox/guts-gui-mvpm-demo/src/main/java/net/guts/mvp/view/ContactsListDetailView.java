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

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
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
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;

@Singleton
public class ContactsListDetailView extends JPanel
{
	static final private long serialVersionUID = 7068262166438989381L;

	@Inject public ContactsListDetailView(AllContactsPM model, 
		ContactViewFactory contactViewFactory,
		DialogFactory dialogFactory, MessageFactory messageFactory)
	{
		_dialogFactory = dialogFactory;
		_messageFactory = messageFactory;
		_contactViewFactory = contactViewFactory;
		_model = model;
		_selection = model._contacts;
		
		// Initialize components
		_txfFirstName.setEditable(false);
		_txfLastName.setEditable(false);
		_txfBirth.setEditable(false);
		_table.setModel(model._contactsTableModel);

		// Setup components bindings
		GutsBindings.bind(_table, _selection);
		_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GutsBindings.bind(_txfFirstName, model._selection._firstName);
		GutsBindings.bind(_txfLastName, model._selection._lastName);
		GutsBindings.bind(_txfBirth, model._selection._birth);
		GutsBindings.bind(_txaAddress, model._selection._homeAddress._compactAddress);
		connectTitle(this, model._selection._title);

		//TODO move out of the View!
		// Connect actions to existence of selection
		connectEmpty(_selection, _delete, _modify);
		GutsBindings.bindDoubleClick(_table, _modify);
		GutsBindings.bindEnter(_table, _modify);
		
		// Layout the form
		//TODO add more info (address)
		DesignGridLayout layout = new DesignGridLayout(this);
		layout.withoutConsistentWidthAcrossNonGridRows();
		layout.row().center().add(new JScrollPane(_table));
		layout.emptyRow();
		layout.row().grid(_lblFirstName).add(_txfFirstName);
		layout.row().grid(_lblLastName).add(_txfLastName);
		layout.row().grid(_lblBirth).add(_txfBirth);
		layout.row().grid(_lblAddress).add(_txaAddress);
		layout.emptyRow();
		layout.row().right().add(_btnCreate, _btnModify, _btnDelete);
	}
	
	//TODO put to some utility class in guts-properties!
	static void connectEmpty(final SelectionInList<?> selection, final Action... actions)
	{
		selection.addPropertyChangeListener(
			SelectionInList.PROPERTYNAME_SELECTION_EMPTY, new PropertyChangeListener()
		{
			@Override public void propertyChange(PropertyChangeEvent evt)
			{
				for (Action action: actions)
				{
					action.setEnabled(!selection.isSelectionEmpty());
				}
			}
		});
		for (Action action: actions)
		{
			action.setEnabled(!selection.isSelectionEmpty());
		}
	}
	
	//TODO put to some util class?
	static void connectTitle(final JComponent view, final ValueModel<String> model)
	{
		if (view.getRootPane() != null)
		{
			PropertyConnector.connectAndUpdate(model, view.getRootPane().getParent(), "title");
		}
		view.addHierarchyListener(new HierarchyListener()
		{
			@Override public void hierarchyChanged(HierarchyEvent e)
			{
				PropertyConnector.connectAndUpdate(model, view.getRootPane().getParent(), "title");
			}
		});
	}

	//TODO move all actions out of View!???
	
	final private GutsAction _create = new GutsAction()
	{
		@Override protected void perform()
		{
			// ask PM to create new ContactPM
			ContactPM contactModel = _model.createContact();
			OkCancel template = OkCancel.create().withOK(_model.saveContact(contactModel)).withCancel();
			JDialogConfig config = JDialogConfig.create().merge(template);
			ContactView view = _contactViewFactory.create(contactModel);
			_dialogFactory.showDialog(view, config.config());
		}
	};
	
	final private GutsAction _modify = new GutsAction()
	{
		@Override protected void perform()
		{
			OkCancel template = OkCancel.create().withOK(_model._saveSelectedContact).withCancel();
			JDialogConfig config = JDialogConfig.create().merge(template);
			ContactView view = _contactViewFactory.create(_model._selection);
			_dialogFactory.showDialog(view, config.config());
		}
	};
	
	final private GutsAction _delete = new TaskAction()
	{
		@Override protected void perform()
		{
			// First ask user confirmation
			Contact selection = _selection.getSelection();
			if (UserChoice.YES == _messageFactory.showMessage(
				"confirm-delete", selection.getFirstName(), selection.getLastName()))
			{
				submit(_model.getDeleteTask(), InputBlockers.actionBlocker(this));
			}
		}
	};
	
	final private DialogFactory _dialogFactory;
	final private MessageFactory _messageFactory;
	
	final private ContactViewFactory _contactViewFactory;
	
	final private AllContactsPM _model;
	final private SelectionInList<Contact> _selection;
	
	final private JTable _table = new JTable();
	final private JLabel _lblFirstName = new JLabel();
	final private JTextField _txfFirstName = new JTextField();
	final private JLabel _lblLastName = new JLabel();
	final private JTextField _txfLastName = new JTextField();
	final private JLabel _lblBirth = new JLabel();
	final private JFormattedTextField _txfBirth = new JFormattedTextField();
	final private JLabel _lblAddress = new JLabel();
	final private JTextArea _txaAddress = new JTextArea(4, 40);
	final private JButton _btnModify = new JButton(_modify);
	final private JButton _btnCreate = new JButton(_create);
	final private JButton _btnDelete = new JButton(_delete);
}
