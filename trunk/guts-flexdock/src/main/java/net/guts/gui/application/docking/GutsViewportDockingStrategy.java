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

import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingStrategy;
import org.flexdock.docking.drag.DragOperation;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

import net.guts.event.Channel;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
class GutsViewportDockingStrategy extends DefaultDockingStrategy implements ViewportFactory
{
	@Inject GutsViewportDockingStrategy(
		Channel<View> selectedView, EmptyViewsRegistry emptyViewsRegistry,
		Provider<GutsViewport> portProvider)
	{
		_selectedView = selectedView;
		_emptyViewsRegistry = emptyViewsRegistry;
		_portProvider = portProvider;
	}
	
	@Override protected DockingPort createDockingPortImpl(DockingPort parent)
	{
		return createViewport();
	}
	
	@Override public Viewport createViewport()
	{
		GutsViewport port = _portProvider.get();
		ViewChangeListener listener = new ViewChangeListener();
		port.setListener(listener);
		listener.setViewport(port);
		return port;
	}
	
	@SuppressWarnings("unchecked") 
	@Override public boolean dock(
		Dockable dockable, DockingPort port, String region, DragOperation operation)
	{
		try
		{
			_disableListener++;
			boolean result = super.dock(dockable, port, region, operation);
			GutsViewport viewport = (GutsViewport) port;
			if (	result
				&&	viewport.isEmptyablePort()
				&&	CENTER_REGION.equals(region))
			{
				View empty = _emptyViewsRegistry.getEmptyView(viewport);
				if (empty != null && dockable != empty)
				{
					super.undock(empty);
					// Make sure to show tabs when port is not empty
					viewport.getDockingProperties().setSingleTabsAllowed(true);
				}
				// Push selection event
				viewChanged((View) dockable);
			}
			if (result && CENTER_REGION.equals(region))
			{
				// Workaround to flexdock bug in showing the right tabText in some
				// circumstances. Force change of tab text for all dockables in port
				Set<View> views = viewport.getViewset();
				for (View view: views)
				{
					String tabText = view.getTabText();
					view.setTabText("");
					view.setTabText(tabText);
				}
			}
			return result;
		}
		finally
		{
			_disableListener--;
		}
	}

	@Override public boolean undock(Dockable dockable)
	{
		try
		{
			_disableListener++;
			if (dockable == null)
			{
				return false;
			}
			boolean mustCallViewChanged = false;
			View view = (View) dockable;
			GutsViewport port = (GutsViewport) view.getDockingPort();
			if (port != null && port.isEmptyablePort())
			{
				if (port.getViewset().size() == 1)
				{
					super.dock(_emptyViewsRegistry.getEmptyView(port), port, 
						DockingManager.CENTER_REGION);
					// Make sure to NOT show tabs when port is empty
					port.getDockingProperties().setSingleTabsAllowed(false);
					// Push board area selection event
					viewChanged(null);
				}
				else
				{
					mustCallViewChanged = true;
				}
			}
			boolean result = super.undock(dockable);
			if (mustCallViewChanged)
			{
				// Need to call viewChanged with the new view
				viewChanged((View) port.getDockable(CENTER_REGION));
			}
			return result;
		}
		finally
		{
			_disableListener--;
		}
	}
	
	protected void viewChanged(View view)
	{
		_selectedView.publish(view);
	}
	
	protected class ViewChangeListener implements ChangeListener
	{
		public void	setViewport(GutsViewport port)
		{
			_port = port;
		}

		public void stateChanged(ChangeEvent evt)
		{
			if (_disableListener == 0 && _port.isEmptyablePort())
			{
				View view = (View) _port.getDockable(CENTER_REGION);
				viewChanged(view);
			}
		}
		
		private GutsViewport _port;
	}
	
	private final Channel<View> _selectedView;
	private final Provider<GutsViewport> _portProvider;
	private final EmptyViewsRegistry _emptyViewsRegistry;
	protected int _disableListener = 0;
}
