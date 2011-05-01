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

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.guts.binding.GutsBindings;
import net.guts.binding.Models;
import net.guts.mvp.presenter.ContactPM;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ContactView extends JPanel
{
	static final private long serialVersionUID = -1436540113538430985L;
	
	@Inject ContactView(@Assisted ContactPM model)
	{
		// Widgets setup
		home = new AddressView(model.homeAddress);
		office = new AddressView(model.officeAddress);

		// Bind the widgets to the model
		GutsBindings.bind(txfFirstName, model.firstName);
		GutsBindings.bind(txfLastName, model.lastName);
		GutsBindings.bind(txfBirth, model.birth);
		//FIXME hereafter we want one-way-only binding not bidirectional!!!
		GutsBindings.connectIcon(picture, model.picture);
		GutsBindings.connectText(picture, model.pictureText);
		
		// Layout the view
		DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().grid(lblFirstName).add(txfFirstName).grid().add(picture);
		layout.row().grid(lblLastName).add(txfLastName).grid().spanRow();
		layout.row().grid(lblBirth).add(txfBirth).grid().spanRow();
		home.layout(layout, true);
		office.layout(layout, true);
	}

	final private JLabel lblFirstName = new JLabel();
	final private JTextField txfFirstName = new JTextField();
	final private JLabel lblLastName = new JLabel();
	final private JTextField txfLastName = new JTextField();
	final private JLabel lblBirth = new JLabel();
	final private JFormattedTextField txfBirth = new JFormattedTextField();
	final private JLabel picture = new JLabel();
	final private AddressView home;
	final private AddressView office;
}
