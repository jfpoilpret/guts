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

import java.awt.Component;
import java.awt.Container;
import java.util.Set;

import javax.swing.JTabbedPane;

import org.flexdock.docking.event.hierarchy.DockingPortTracker;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DockingHelper
{
	static private final Logger _logger = LoggerFactory.getLogger(DockingHelper.class);

	private DockingHelper()
	{
	}
	
	@SuppressWarnings("unchecked") 
	static public Viewport findEmptyableViewport(String idEmptyView)
	{
		// Check all viewports
		Set<GutsViewport> ports = DockingPortTracker.getDockingPorts();
		for (GutsViewport port: ports)
		{
			if (idEmptyView.equals(port.getEmptyViewId()))
			{
				return port;
			}
		}
		return null;
	}

	static public boolean selectView(Viewport port, String id)
	{
		// Get current focused component
//		KeyboardFocusManager focusManager =
//					KeyboardFocusManager.getCurrentKeyboardFocusManager();
//		Component focused = focusManager.getPermanentFocusOwner();
//		try
//		{
			Component docked = port.getDockedComponent();
			if (docked instanceof JTabbedPane)
			{
				JTabbedPane tabs = (JTabbedPane) docked;
				for (Component tab: tabs.getComponents())
				{
					View view = (View) tab;
					if (view.getPersistentId().equals(id))
					{
						tabs.setSelectedComponent(view);
						return true;
					}
				}
			}
			else if (docked instanceof View)
			{
				return ((View) docked).getPersistentId().equals(id);
			}
			return false;
//		}
//		finally
//		{
//			// Restore previously focused component
//			if (focused != null)
//			{
//				focused.requestFocusInWindow();
//			}
//		}
	}

	static public void trace(Component comp, String indent)
	{
		if (comp instanceof GutsViewport)
		{
			String debug = String.format("%sGutsViewport(emptyViewId=%s)", 
				indent, ((GutsViewport) comp).getEmptyViewId());
			_logger.debug(debug);
		}
		else if (comp instanceof View)
		{
			String debug = String.format("%sView(id=%s)", 
				indent, ((View) comp).getPersistentId());
			_logger.debug(debug);
		}
		if (comp instanceof Container)
		{
			for (Component child: ((Container) comp).getComponents())
			{
				trace(child, indent + "  ");
			}
		}
	}
}
