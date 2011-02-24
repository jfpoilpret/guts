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

import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.guts.demo.addressbook.singleframe.domain.Address;
import net.guts.demo.addressbook.singleframe.domain.Contact;
import net.guts.gui.naming.ComponentHolder;
import net.java.dev.designgridlayout.DesignGridLayout;

public class ContactDetailTabPanel extends JPanel
{
	static final private long serialVersionUID = -2466527938965223045L;

	public ContactDetailTabPanel()
    {
	    _tabs.add(_contactTab);
	    _tabs.add(_home);
	    _tabs.add(_office);

	    DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().grid().add(_tabs);
    }

	public void modelToView(Contact contact)
	{
		if (contact == null)
		{
			_contact = new Contact();
		}
		else
		{
			_contact = contact.copy();
		}
		_contactTab.setContact(_contact);
		_home.setAddress(_contact.getHome());
		_office.setAddress(_contact.getOffice());
	}
	
	public Contact viewToModel()
	{
		// Force accept on every tab
		_contactTab.accept();
		_home.accept();
		_office.accept();
		return _contact;
	}

	private final JTabbedPane _tabs = new JTabbedPane();
	private final ContactTab _contactTab = new ContactTab();
	private final AddressTab _home = new AddressTab();
	private final AddressTab _office = new AddressTab();
	private Contact _contact;
}

class ContactTab extends JPanel implements ComponentHolder
{
	static final private long serialVersionUID = 1402493075589899746L;

	public ContactTab()
	{
		// Layout panel
		DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().grid(_lblFirstName).add(_txfFirstName);
		layout.row().grid(_lblLastName).add(_txfLastName);
		layout.row().grid(_lblBirth).add(_txfBirth);
	}

	public void setContact(Contact contact)
	{
		_contact = contact;
		_txfFirstName.setText(contact.getFirstName());
		_txfLastName.setText(contact.getLastName());
		_txfBirth.setValue(contact.getBirth());
	}

	public void accept()
    {
		// Binding from Swing to Domain
		_contact.setFirstName(_txfFirstName.getText());
		_contact.setLastName(_txfLastName.getText());
		_contact.setBirth((Date) _txfBirth.getValue());
    }
	
	final private JLabel _lblFirstName = new JLabel();
	final private JTextField _txfFirstName = new JTextField(20);
	final private JLabel _lblLastName = new JLabel();
	final private JTextField _txfLastName = new JTextField(20);
	final private JLabel _lblBirth = new JLabel();
	final private JFormattedTextField _txfBirth = new JFormattedTextField();
	private Contact _contact;
}

class AddressTab extends JPanel implements ComponentHolder
{
	static final private long serialVersionUID = -8039187194458016644L;

	public AddressTab()
	{
		DesignGridLayout layout = new DesignGridLayout(this);
		_addressPane.layout(layout, false);
	}
	
	public void setAddress(Address address)
	{
		_address = address;
		_addressPane.setAddress(address);
	}

	public void accept()
    {
		_addressPane.updateAddress(_address);
    }
	
	private final AddressPanel _addressPane = new AddressPanel();
	private Address _address;
}
