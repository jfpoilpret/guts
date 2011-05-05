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

package net.guts.mvpm.view;

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

import net.guts.mvpm.action.AllContactsUiActions;
import net.guts.mvpm.pm.AllContactsPM;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AllContactsView extends JPanel
{
	static final private long serialVersionUID = 7068262166438989381L;

	@Inject AllContactsView(AllContactsPM model, AllContactsUiActions uiActions)
	{
		// Initialize components
		txfFirstName.setEditable(false);
		txfLastName.setEditable(false);
		txfBirth.setEditable(false);
		table.setModel(model.contactsTableModel);

		// Setup components bindings
		GutsBindings.bind(table, model.contacts);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		GutsBindings.bind(txfFirstName, model.selection.firstName);
		GutsBindings.bind(txfLastName, model.selection.lastName);
		GutsBindings.bind(txfBirth, model.selection.birth);
		GutsBindings.bind(txaAddress, model.selection.homeAddress.compactAddress);
		GutsBindings.connectTitle(this, model.selection.title);
		btnCreate.setAction(uiActions.create);
		btnModify.setAction(uiActions.modify);
		btnDelete.setAction(uiActions.delete);

		GutsBindings.connectDoubleClick(table, uiActions.modify);
		GutsBindings.connectEnter(table, uiActions.modify);
		
		// Layout the form
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
	
	final private JTable table = new JTable();
	final private JLabel lblFirstName = new JLabel();
	final private JTextField txfFirstName = new JTextField();
	final private JLabel lblLastName = new JLabel();
	final private JTextField txfLastName = new JTextField();
	final private JLabel lblBirth = new JLabel();
	final private JFormattedTextField txfBirth = new JFormattedTextField();
	final private JLabel lblAddress = new JLabel();
	final private JTextArea txaAddress = new JTextArea(4, 40);
	final private JButton btnModify = new JButton();
	final private JButton btnCreate = new JButton();
	final private JButton btnDelete = new JButton();
}
