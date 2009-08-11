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

import org.jdesktop.application.Task;

import net.guts.gui.addressbook.business.AddressBookService;
import net.guts.gui.addressbook.domain.Address;
import net.guts.gui.addressbook.domain.Contact;
import net.guts.gui.dialog.ParentDialog;
import net.guts.gui.dialog.support.AbstractWizardPanel;
import net.guts.gui.dialog.support.AbstractWizardStepPanel;
import net.guts.gui.dialog.support.Acceptor;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContactWizardPanel extends AbstractWizardPanel implements Acceptor
{
	static final private long serialVersionUID = -8845317327842500636L;
	static final private String NAME = "ContactDetailWizardPanel";

	public ContactWizardPanel()
    {
	    super(NAME);
    }

	@Override protected void initWizard()
    {
	    getController().addWizardPane(_contactPane, true);
	    getController().addWizardPane(_homePane, true);
	    getController().addWizardPane(_officePane, true);
	    setContact(null);
    }

	public void setContact(Contact contact)
	{
		if (contact == null)
		{
			_contact = new Contact();
		    _create = true;
		}
		else
		{
			_contact = contact.copy();
		    _create = false;
		}
		getController().setContext(_contact);
	}

	public Task<Void, Void> accept(ParentDialog parent)
    {
	    // Now save
		if (_create)
		{
			_service.createContact(_contact);
		}
		else
		{
			_service.modifyContact(_contact);
		}
		parent.close(false);
	    return null;
    }

	private final ContactPane _contactPane = new ContactPane();
	private final AbstractAddressPane _homePane = 
		new HomeAddressPane(NAME + "-home", false);
	private final AbstractAddressPane _officePane = 
		new OfficeAddressPane(NAME + "-office", true);
	private Contact _contact;
	private boolean _create;
	@Inject private AddressBookService _service;
}

class ContactPane extends AbstractWizardStepPanel
{
	static final private long serialVersionUID = 4307142630118960207L;
	static final private String NAME = "ContactDetailWizardPanel-contact";

	public ContactPane()
	{
		setName(NAME);
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
	}

	@Override public void enter()
    {
		_contact = getController().getContext(Contact.class);
		_txfFirstName.setText(_contact.getFirstName());
		_txfLastName.setText(_contact.getLastName());
		_txfBirth.setValue(_contact.getBirth());
    }

	@Override public <T, V> Task<T, V> leave()
    {
		// Binding from Swing to Domain
		_contact.setFirstName(_txfFirstName.getText());
		_contact.setLastName(_txfLastName.getText());
		_contact.setBirth((Date) _txfBirth.getValue());
	    return null;
    }

	final private JLabel _lblFirstName = new JLabel();
	final private JTextField _txfFirstName = new JTextField(20);
	final private JLabel _lblLastName = new JLabel();
	final private JTextField _txfLastName = new JTextField(20);
	final private JLabel _lblBirth = new JLabel();
	final private JFormattedTextField _txfBirth = new JFormattedTextField();
	private Contact _contact;
}

abstract class AbstractAddressPane extends AbstractWizardStepPanel
{
	static final private long serialVersionUID = -5584247386479109165L;

	protected AbstractAddressPane(String id, boolean last)
	{
		setName(id);
		_last = last;
		_addressPane = new AddressPanel(id);
		DesignGridLayout layout = new DesignGridLayout(this);
		setLayout(layout);
		_addressPane.layout(layout, false);
	}
	
	abstract protected Address getAddress(Contact contact);
	
	@Override public void enter()
    {
		_address = getAddress(getController().getContext(Contact.class));
		_addressPane.setAddress(_address);
    }

	@Override public <T, V> Task<T, V> leave()
    {
		_addressPane.updateAddress(_address);
		getController().setAcceptEnabled(_last);
		return null;
    }
	
	private final AddressPanel _addressPane;
	private final boolean _last;
	private Address _address;
}

class HomeAddressPane extends AbstractAddressPane
{
	static final private long serialVersionUID = -5631169946204437002L;

	protected HomeAddressPane(String id, boolean last)
    {
	    super(id, last);
    }

	@Override protected Address getAddress(Contact contact)
    {
	    return contact.getHome();
    }
}

class OfficeAddressPane extends AbstractAddressPane
{
	static final private long serialVersionUID = -7544475684452085579L;

	protected OfficeAddressPane(String id, boolean last)
    {
	    super(id, last);
    }

	@Override protected Address getAddress(Contact contact)
    {
	    return contact.getOffice();
    }
}
