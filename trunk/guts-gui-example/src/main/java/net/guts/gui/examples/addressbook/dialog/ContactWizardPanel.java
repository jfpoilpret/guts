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

package net.guts.gui.examples.addressbook.dialog;

import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.support.AbstractWizardPanel;
import net.guts.gui.dialog.support.AbstractWizardStepPanel;
import net.guts.gui.examples.addressbook.business.AddressBookService;
import net.guts.gui.examples.addressbook.domain.Address;
import net.guts.gui.examples.addressbook.domain.Contact;
import net.guts.gui.naming.ComponentHolder;
import net.guts.gui.task.Task;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContactWizardPanel extends AbstractWizardPanel
{
	static final private long serialVersionUID = -8845317327842500636L;
	static final private String NAME = "ContactDetailWizardPanel";

	public ContactWizardPanel()
    {
	    setName(NAME);
    }

	@Override protected GutsAction getAcceptAction()
	{
		return _accept;
	}

	@Override protected void initWizard()
    {
	    getController().addWizardPane(_contactPane, true);
	    getController().addWizardPane(_home, true);
	    getController().addWizardPane(_office, true);
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

	private final GutsAction _accept = new GutsAction()
	{
		@Override protected void perform()
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
			getParentDialog().close(false);
		}
	};
	
	private final ContactPane _contactPane = new ContactPane();
	private final AbstractAddressPane _home = 
		new HomeAddressPane(NAME + "-home", false);
	private final AbstractAddressPane _office = 
		new OfficeAddressPane(NAME + "-office", true);
	private Contact _contact;
	private boolean _create;
	@Inject private AddressBookService _service;
}

class ContactPane extends AbstractWizardStepPanel implements ComponentHolder
{
	static final private long serialVersionUID = 4307142630118960207L;

	public ContactPane()
	{
		// Layout panel
		DesignGridLayout layout = new DesignGridLayout(this);
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

	@Override public <T> Task<T> leave()
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

abstract class AbstractAddressPane extends AbstractWizardStepPanel implements ComponentHolder
{
	static final private long serialVersionUID = -5584247386479109165L;

	protected AbstractAddressPane(String id, boolean last)
	{
		_last = last;
		DesignGridLayout layout = new DesignGridLayout(this);
		_addressPane.layout(layout, false);
	}
	
	abstract protected Address getAddress(Contact contact);
	
	@Override public void enter()
    {
		_address = getAddress(getController().getContext(Contact.class));
		_addressPane.setAddress(_address);
    }

	@Override public <T> Task<T> leave()
    {
		_addressPane.updateAddress(_address);
		getController().setAcceptEnabled(_last);
		return null;
    }
	
	private final AddressPanel _addressPane = new AddressPanel();
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
