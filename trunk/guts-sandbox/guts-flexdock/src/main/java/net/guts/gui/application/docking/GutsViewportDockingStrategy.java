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
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.event.EventManager;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.event.Channel;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
class GutsViewportDockingStrategy extends DefaultDockingStrategy 
implements ViewportFactory, ViewFactoryListener
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
		_logger.debug("createViewport()");
		GutsViewport port = _portProvider.get();
		ViewChangeListener listener = new ViewChangeListener();
		port.setListener(listener);
		listener.setViewport(port);
		port.addDockingListener(_dockListener);
		return port;
	}
	
	@Override public void viewCreated(View view)
	{
		view.addDockingListener(_dockListener);
	}
	
	@SuppressWarnings("unchecked") 
	@Override public boolean dock(
		Dockable dockable, DockingPort port, String region, DragOperation operation)
	{
		_logger.debug("dock(view(id={}), port(emptyId={}), {}) _disableListener = {}", 
			new Object[]
			{
				dockable.getPersistentId(),
				((GutsViewport) port).getEmptyViewId(),
				region,
				_disableListener
			});
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
					// Make sure to show tabs when port is not empty
					viewport.getDockingProperties().setSingleTabsAllowed(true);
					super.undock(empty);
				}
				// Push selection event
				viewChanged((View) dockable);
			}
			if (result && CENTER_REGION.equals(region))
			{
				// Workaround to flexdock bug in showing the right tabText in some
				// circumstances. Force change of tab text for all dockables in port
				Set<View> views = viewport.getViewset(0);
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
		_logger.debug("undock(view(id={})) _disableListener = {}", 
			new Object[]{dockable.getPersistentId(), _disableListener});
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
				if (port.getViewset(0).size() == 1)
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
	
	private void viewChanged(View view)
	{
		_selectedView.publish(view);
	}
	
	private class ViewChangeListener implements ChangeListener
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
	
	private void updateViewportsStates()
	{
		_logger.debug("updateViewportsStates() _disableListener = {}, _dropInProgress = {}",
			new Object[]{_disableListener, _dropInProgress});
		if (_disableListener == 0 && _dropInProgress == 0)
		{
			GutsViewport.updateAllPortsStates();
		}
	}

	// This listener has some tricks to make it work correctly.
	// On drag/drop gestures, the following sequence of call/events occur:
	// - dropStarted
	// - undock()
	// - undockingComplete
	// - dock()
	// - dockingComplete
	// Viewports state update should occur only in the end (otherwise flexdock internal
	// data is stale)
	// That's why we use the _dropInProgress flag.
	//TODO need to listen to other events (canceled dock/undock to clear _dropInProcess)?
	private class DockListener extends DockingListener.Stub
	{
		@Override public void dropStarted(DockingEvent evt)
		{
			_logger.debug("dropStated()");
			_dropInProgress = 3;
		}

		@Override public void dockingComplete(DockingEvent evt)
		{
			_logger.debug("dockingComplete()");
			if (_dropInProgress > 0)
			{
				_dropInProgress--;
			}
			updateViewportsStates();
		}

		@Override public void undockingComplete(DockingEvent evt)
		{
			_logger.debug("undockingComplete()");
			updateViewportsStates();
		}
	}

	static final private Logger _logger = 
		LoggerFactory.getLogger(GutsViewportDockingStrategy.class);

	private final DockListener _dockListener = new DockListener();
	private final Channel<View> _selectedView;
	private final Provider<GutsViewport> _portProvider;
	private final EmptyViewsRegistry _emptyViewsRegistry;
	private int _disableListener = 0;
	private int _dropInProgress = 0;
}
