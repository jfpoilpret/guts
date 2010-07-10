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

import org.flexdock.view.View;
import org.flexdock.view.Viewport;

import net.guts.gui.application.docking.DockingHelper;
import net.guts.gui.examples.addressbook.domain.Contact;

final public class ViewHelper
{
	private ViewHelper()
	{
	}

	static public boolean isContactPictureViewId(String id)
	{
		return id.startsWith(Views.ContactPicture.name());
	}
	
	static public String getContactPictureViewId(Contact contact)
	{
		return Views.ContactPicture.name() + contact.getId();
	}
	
	static public Integer getContactIdFromView(View view)
	{
		if (view != null)
		{
			String id = view.getPersistentId();
			if (isContactPictureViewId(id))
			{
				id = id.substring(Views.ContactPicture.name().length());
				if (!"".equals(id))
				{
					return Integer.parseInt(id);
				}
			}
		}
		return null;
	}
	
	static public boolean selectPictureView(Contact contact)
	{
		if (contact != null)
		{
			Viewport port = DockingHelper.findEmptyableViewport(Views.ContactPicture.name());
			return DockingHelper.selectView(port, getContactPictureViewId(contact));
		}
		else
		{
			return false;
		}
	}
}
