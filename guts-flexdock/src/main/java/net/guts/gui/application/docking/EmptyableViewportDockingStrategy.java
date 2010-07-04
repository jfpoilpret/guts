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

public class EmptyableViewportDockingStrategy
extends DefaultDockingStrategy implements ViewportFactory
{
	//TODO if this class becomes package-private change method into ctor!
	@Inject void init(Channel<View> selectedView)
	{
		_selectedView = selectedView;
	}
	
	@Override protected DockingPort createDockingPortImpl(DockingPort parent)
	{
		return createViewport();
	}
	
	@Override final public Viewport createViewport()
	{
		EmptyableViewport port = createViewportImpl();
		EmptyableViewChangeListener listener = new EmptyableViewChangeListener();
		port.setListener(listener);
		listener.setViewport(port);
		return port;
	}
	
	protected EmptyableViewport createViewportImpl()
	{
		return new EmptyableViewport();
	}

	@SuppressWarnings("unchecked") 
	@Override public boolean dock(
		Dockable dockable, DockingPort port, String region, DragOperation operation)
	{
		try
		{
			_disableListener++;
			boolean result = super.dock(dockable, port, region, operation);
			EmptyableViewport viewport = (EmptyableViewport) port;
			if (	result
				&&	viewport.isEmptyablePort()
				&&	CENTER_REGION.equals(region))
			{
				if (	dockable != getEmptyView()
					&&	viewport.getViewset().contains(getEmptyView()))
				{
					super.undock(getEmptyView());
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
			boolean mustCallViewChanged = false;
			_disableListener++;
			if (dockable == null)
			{
				return false;
			}
			View view = (View) dockable;
			EmptyableViewport port = (EmptyableViewport) view.getDockingPort();
			if (port != null && port.isEmptyablePort())
			{
				if (port.getViewset().size() == 1)
				{
					super.dock(getEmptyView(), port, DockingManager.CENTER_REGION);
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
	
	final protected Dockable getEmptyView()
	{
		if (_emptyView == null)
		{
			_emptyView = DockingManager.getDockableFactory().getDockable(
				EmptyableViewport.EMPTY_VIEW_ID);
		}
		return _emptyView;
	}

	protected class EmptyableViewChangeListener implements ChangeListener
	{
		public void	setViewport(EmptyableViewport port)
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
		
		private EmptyableViewport _port;
	}
	
	protected Channel<View> _selectedView;
	protected Dockable _emptyView = null;
	protected int _disableListener = 0;
}
