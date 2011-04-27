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
		lblCity.setHorizontalAlignment(JLabel.RIGHT);
		
		// Bind components to the PM
		Bindings.bind(txfStreet1, model.street1);
		Bindings.bind(txfStreet2, model.street2);
		Bindings.bind(txfZip, model.zip);
		Bindings.bind(txfCity, model.city);
		Bindings.bind(txfPhone, model.phone);
	}

	public void layout(DesignGridLayout layout)
	{
		layout(layout, true);
	}
	
	public void layout(DesignGridLayout layout, boolean separator)
	{
		if (separator)
		{
			header.layout(layout);
		}
		layout.row().grid(lblStreet1).add(txfStreet1);
		layout.row().grid(lblStreet2).add(txfStreet2);
		layout.row().grid(lblZip).add(txfZip).grid(lblCity).add(txfCity);
		layout.row().grid(lblPhone).add(txfPhone);
	}
	
	final private GroupHeader header = GroupHeader.create();
	final private JLabel lblStreet1 = new JLabel();
	final private JTextField txfStreet1 = new JTextField(32);
	final private JLabel lblStreet2 = new JLabel();
	final private JTextField txfStreet2 = new JTextField(32);
	final private JLabel lblZip = new JLabel();
	final private JTextField txfZip = new JTextField(8);
	final private JLabel lblCity = new JLabel();
	final private JTextField txfCity = new JTextField(20);
	final private JLabel lblPhone = new JLabel();
	final private JTextField txfPhone = new JTextField(10);
}
