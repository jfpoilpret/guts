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

import net.guts.gui.application.docking.EmptyableViewportPolicy;
import net.guts.gui.examples.addressbook.view.ContactPictureView;

public class AddressBookViewportPolicy implements EmptyableViewportPolicy
{
	@Override public boolean isEmptyView(String idView)
	{
		return Views.ContactPicture.name().equals(idView);
	}

	@Override public boolean isTargetForEmptyableViewport(String idView, View view)
	{
		return isEmptyView(idView) && view.getContentPane() instanceof ContactPictureView;
	}
}
