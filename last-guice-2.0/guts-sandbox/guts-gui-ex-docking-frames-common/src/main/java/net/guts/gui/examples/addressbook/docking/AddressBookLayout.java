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

package net.guts.gui.examples.addressbook.docking;

import net.guts.gui.docking.LayoutInitializer;
import net.guts.gui.docking.ViewFactory;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.location.CBaseLocation;

public class AddressBookLayout implements LayoutInitializer
{
	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.docking.LayoutInitializer#initLayout(bibliothek.gui.dock.common.CControl, net.guts.gui.docking.ViewFactory)
	 */
	@Override public void initLayout(CControl control, ViewFactory factory)
	{
		CBaseLocation base = CLocation.base(control.getContentArea());

		SingleCDockable pictureArea = control.getSingleDockable(Views.ContactPicture.name());
		pictureArea.setLocation(base.normal());
		pictureArea.setVisible(true);

		SingleCDockable contactsList = factory.createSingle(Views.ContactList.name());
		control.add(contactsList);
		contactsList.setLocation(base.normalWest(0.33));
		contactsList.setVisible(true);

		SingleCDockable contactDetail = factory.createSingle(Views.ContactDetail.name());
		control.add(contactDetail);
		contactDetail.setLocation(base.normalWest(0.33).south(0.5));
		contactDetail.setVisible(true);
	}
}
