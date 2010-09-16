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

package net.guts.gui.application.docking;

import org.flexdock.docking.DockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;

// Fix for Flexdock bug if drop on forbidden port
class GutsDockingListener extends DockingListener.Stub
{
	@Override public void dropStarted(DockingEvent evt)
	{
		DockingPort port = evt.getNewDockingPort();
		if (	port != null
			&&	!port.isDockingAllowed(evt.getComponent(), evt.getRegion()))
		{
			evt.consume();
		}
	}
}
