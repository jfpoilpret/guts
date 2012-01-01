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

import javax.swing.JPanel;

import net.guts.demo.addressbook.singleframe.domain.Address;
import net.guts.demo.addressbook.singleframe.domain.Contact;
import net.guts.gui.naming.ComponentHolder;
import net.java.dev.designgridlayout.DesignGridLayout;

abstract public class AbstractAddressStep extends JPanel implements ComponentHolder
{
	static final private long serialVersionUID = -5584247386479109165L;

	protected AbstractAddressStep()
	{
		DesignGridLayout layout = new DesignGridLayout(this);
		_addressPane.layout(layout, false);
	}
	
	abstract protected Address getAddress(Contact contact);

	public void modelToView(Contact contact)
    {
		_address = getAddress(contact);
		_addressPane.setAddress(_address);
    }

	public void accept()
	{
		_addressPane.updateAddress(_address);
    }
	
	private final AddressPanel _addressPane = new AddressPanel();
	private Address _address;
}