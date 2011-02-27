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

package net.guts.demo.addressbook.singleframe.dialog;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.guts.demo.addressbook.singleframe.domain.Address;
import net.guts.gui.naming.ComponentHolder;
import net.guts.gui.util.GroupHeader;
import net.java.dev.designgridlayout.DesignGridLayout;

public class AddressPanel implements ComponentHolder
{
	public AddressPanel()
	{
		_lblCity.setHorizontalAlignment(JLabel.RIGHT);
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
	
	public void reset()
	{
		_txfStreet1.setText("");
		_txfStreet2.setText("");
		_txfZip.setText("");
		_txfCity.setText("");
		_txfPhone.setText("");
	}
	
	public void setAddress(Address address)
	{
		_txfStreet1.setText(address.getStreet1());
		_txfStreet2.setText(address.getStreet2());
		_txfZip.setText(address.getZip());
		_txfCity.setText(address.getCity());
		_txfPhone.setText(address.getPhone());
	}
	
	public void updateAddress(Address address)
	{
		address.setStreet1(_txfStreet1.getText());
		address.setStreet2(_txfStreet2.getText());
		address.setZip(_txfZip.getText());
		address.setCity(_txfCity.getText());
		address.setPhone(_txfPhone.getText());
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
