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

import net.guts.mvp.presenter.ContactPM;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.jgoodies.binding.adapter.Bindings;

public class ContactView extends JPanel
{
	static final private long serialVersionUID = -1436540113538430985L;
	
	@Inject public ContactView(@Assisted ContactPM model)
	{
		// Widgets setup
		_home = new AddressView(model._homeAddress);
		_office = new AddressView(model._officeAddress);

		// Bind the widgets to the model
		Bindings.bind(_txfFirstName, model._firstName);
		Bindings.bind(_txfLastName, model._lastName);
		Bindings.bind(_txfBirth, model._birth);
		
		// Layout the view
		DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().grid(_lblFirstName).add(_txfFirstName);
		layout.row().grid(_lblLastName).add(_txfLastName);
		layout.row().grid(_lblBirth).add(_txfBirth);
		_home.layout(layout, true);
		_office.layout(layout, true);
	}

	final private JLabel _lblFirstName = new JLabel();
	final private JTextField _txfFirstName = new JTextField();
	final private JLabel _lblLastName = new JLabel();
	final private JTextField _txfLastName = new JTextField();
	final private JLabel _lblBirth = new JLabel();
	final private JFormattedTextField _txfBirth = new JFormattedTextField();
	final private AddressView _home;
	final private AddressView _office;
}
