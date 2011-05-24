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

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.guts.binding.GutsBindings;
import net.guts.binding.Models;
import net.guts.mvpm.pm.ContactPM;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.jgoodies.validation.view.ValidationResultViewFactory;

//TODO realtime validation check (in ContactPM)?
public class ContactView extends JPanel
{
	static final private long serialVersionUID = -1436540113538430985L;
	
	@Inject ContactView(final @Assisted ContactPM model)
	{
		// Widgets setup
		home = new AddressView(model.homeAddress);
		office = new AddressView(model.officeAddress);
		validation = ValidationResultViewFactory.createReportList(model.validation);
		JList list = (JList) ((JScrollPane) validation).getViewport().getView();
		list.setVisibleRowCount(3);
		//TODO improve layout somehow: try to use DGL smart resize
		
		// Set keys for binding of validation results to individual components

		// Bind the widgets to the model
		GutsBindings.bind(txfFirstName, model.firstName);
		GutsBindings.bind(txfLastName, model.lastName);
		GutsBindings.bind(txfBirth, model.birth);
		GutsBindings.connect(picture, Models.of(Picture.class).getIcon(), model.picture);
		GutsBindings.connectTitle(this, model.title);
		
		// Layout the view
		DesignGridLayout layout = new DesignGridLayout(this);
		JScrollPane scroller = new JScrollPane(picture);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		layout.row().grid(lblFirstName).add(txfFirstName).grid().add(scroller);
		layout.row().grid(lblLastName).add(txfLastName).grid().spanRow();
		layout.row().grid(lblBirth).add(txfBirth).grid().spanRow();
		home.layout(layout, true);
		office.layout(layout, true);
		layout.emptyRow();
		layout.row().grid().add(validation);
	}

	final private JLabel lblFirstName = new JLabel();
	final private JTextField txfFirstName = new JTextField();
	final private JLabel lblLastName = new JLabel();
	final private JTextField txfLastName = new JTextField();
	final private JLabel lblBirth = new JLabel();
	final private JFormattedTextField txfBirth = new JFormattedTextField();
	final private Picture picture = new Picture();
	final private AddressView home;
	final private AddressView office;
	final private JComponent validation;
}
