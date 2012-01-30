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

package net.guts.demo.addressbook.docking.docking;

import net.guts.demo.addressbook.docking.domain.Contact;
import net.guts.gui.docking.ViewHelperService;

import bibliothek.gui.dock.common.intern.CDockable;

final public class ViewHelper
{
	private ViewHelper()
	{
	}

	static public boolean isContactPictureViewId(String id)
	{
		return id != null && id.startsWith(Views.ContactPicture.name());
	}
	
	static public String getContactPictureViewId(Contact contact)
	{
		if (contact != null)
		{
			return Views.ContactPicture.name() + contact.getId();
		}	
		else
		{
			return null;
		}
	}
	
	static public Integer getContactIdFromView(ViewHelperService viewHelper, CDockable view)
	{
		String id = viewHelper.getIdFromView(view);
		if (isContactPictureViewId(id))
		{
			return Integer.parseInt(id.substring(Views.ContactPicture.name().length()));
		}
		else
		{
			return null;
		}
	}
}
