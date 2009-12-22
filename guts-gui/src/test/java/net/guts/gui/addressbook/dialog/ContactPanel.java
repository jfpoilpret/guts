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

package net.guts.gui.addressbook.dialog;

import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.guts.gui.addressbook.business.AddressBookService;
import net.guts.gui.addressbook.domain.Contact;
import net.guts.gui.dialog.Resettable;
import net.guts.gui.dialog.support.AbstractPanel;
import net.guts.gui.dialog.support.AcceptGutsAction;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContactPanel extends AbstractPanel implements Resettable
{
	static final private long serialVersionUID = -2653616540903403972L;
	static final private String NAME = "ContactDetailPanel";

	public ContactPanel()
	{
		super(NAME);
		// Initialize components & names
		_lblFirstName.setName(NAME + "-first-name-label");
		_txfFirstName.setName(NAME + "-first-name");
		_lblLastName.setName(NAME + "-last-name-label");
		_txfLastName.setName(NAME + "-last-name");
		_lblBirth.setName(NAME + "-birth-label");
		_txfBirth.setName(NAME + "-birth");

		// Layout panel
		DesignGridLayout layout = new DesignGridLayout(this);
		setLayout(layout);
		layout.row().grid(_lblFirstName).add(_txfFirstName);
		layout.row().grid(_lblLastName).add(_txfLastName);
		layout.row().grid(_lblBirth).add(_txfBirth);

		_home.layout(layout);
		_office.layout(layout);
		
		// Finish initializing AbstractPanel
		initButtons(_accept);
	}
	
	public void reset()
	{
		_contact = null;
		_txfFirstName.setText("");
		_txfLastName.setText("");
		_txfBirth.setValue(null);
		
		_home.reset();
		_office.reset();
	}

	public void setContact(Contact contact)
	{
		_contact = contact;
		_txfFirstName.setText(contact.getFirstName());
		_txfLastName.setText(contact.getLastName());
		_txfBirth.setValue(contact.getBirth());
		
		_home.setAddress(_contact.getHome());
		_office.setAddress(_contact.getOffice());
	}

	private void accept()
    {
		boolean create = false;
		if (_contact == null)
		{
			_contact = new Contact();
			create = true;
		}
		else
		{
			_contact = _contact.copy();
		}
		// Binding from Swing to Domain
		_contact.setFirstName(_txfFirstName.getText());
		_contact.setLastName(_txfLastName.getText());
		_contact.setBirth((Date) _txfBirth.getValue());

		_home.updateAddress(_contact.getHome());
		_office.updateAddress(_contact.getOffice());
		
		if (create)
		{
			_service.createContact(_contact);
		}
		else
		{
			_service.modifyContact(_contact);
		}
		getParentDialog().close(false);
	}

	final private AcceptGutsAction _accept = new AcceptGutsAction()
	{
		@Override protected void perform()
		{
			accept();
		}
	};
	
	final private JLabel _lblFirstName = new JLabel();
	final private JTextField _txfFirstName = new JTextField(20);
	final private JLabel _lblLastName = new JLabel();
	final private JTextField _txfLastName = new JTextField(20);
	final private JLabel _lblBirth = new JLabel();
	final private JFormattedTextField _txfBirth = new JFormattedTextField();
	final private AddressPanel _home = new AddressPanel(NAME + "-home");
	final private AddressPanel _office = new AddressPanel(NAME + "-office");
	private Contact _contact;
	@Inject private AddressBookService _service;
}
