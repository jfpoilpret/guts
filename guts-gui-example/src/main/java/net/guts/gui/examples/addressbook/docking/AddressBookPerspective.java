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

import org.flexdock.docking.DockingConstants;
import org.flexdock.perspective.LayoutSequence;

import net.guts.gui.application.docking.PerspectiveInitializer;

public class AddressBookPerspective implements PerspectiveInitializer
{
	/* (non-Javadoc)
	 * @see net.guts.gui.application.docking.PerspectiveInitializer#getDescription()
	 */
	@Override public String getDescription()
	{
		return "Default Address Book Perspective";
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.docking.PerspectiveInitializer#initLayout(org.flexdock.perspective.LayoutSequence)
	 */
	@Override public void initLayout(LayoutSequence seq)
	{
		seq.add(Views.ContactPicture.name());
		seq.add(Views.ContactList.name(), Views.ContactPicture.name(), 
			DockingConstants.WEST_REGION, 0.33f);
		seq.add(Views.ContactDetail.name(), Views.ContactList.name(), 
			DockingConstants.SOUTH_REGION, 0.5f);
	}
}
