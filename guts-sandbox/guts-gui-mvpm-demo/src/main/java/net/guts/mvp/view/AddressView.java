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

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.guts.gui.naming.ComponentHolder;
import net.guts.gui.util.GroupHeader;
import net.guts.mvp.presenter.AddressPM;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.jgoodies.binding.adapter.Bindings;

public class AddressView implements ComponentHolder
{
	public AddressView(AddressPM model)
	{
		// Initialize components
		_lblCity.setHorizontalAlignment(JLabel.RIGHT);
		
		// Bind components to the PM
		Bindings.bind(_txfStreet1, model._street1);
		Bindings.bind(_txfStreet2, model._street2);
		Bindings.bind(_txfZip, model._zip);
		Bindings.bind(_txfCity, model._city);
		Bindings.bind(_txfPhone, model._phone);
	}

	public void layout(DesignGridLayout layout)
	{
		layout(layout, true);
	}
	
	public void layout(DesignGridLayout layout, boolean separator)
	{
		if (separator)
		{
			_header.layout(layout);
		}
		layout.row().grid(_lblStreet1).add(_txfStreet1);
		layout.row().grid(_lblStreet2).add(_txfStreet2);
		layout.row().grid(_lblZip).add(_txfZip).grid(_lblCity).add(_txfCity);
		layout.row().grid(_lblPhone).add(_txfPhone);
	}
	
	final private GroupHeader _header = GroupHeader.create();
	final private JLabel _lblStreet1 = new JLabel();
	final private JTextField _txfStreet1 = new JTextField(32);
	final private JLabel _lblStreet2 = new JLabel();
	final private JTextField _txfStreet2 = new JTextField(32);
	final private JLabel _lblZip = new JLabel();
	final private JTextField _txfZip = new JTextField(8);
	final private JLabel _lblCity = new JLabel();
	final private JTextField _txfCity = new JTextField(20);
	final private JLabel _lblPhone = new JLabel();
	final private JTextField _txfPhone = new JTextField(10);
}
