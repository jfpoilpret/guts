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
import java.awt.Insets;
import java.util.Set;

import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;

import org.flexdock.docking.DockingManager;
import org.flexdock.docking.activation.ActiveDockableListener;
import org.flexdock.docking.event.TabbedDragListener;
import org.flexdock.util.LookAndFeelSettings;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.ref.WeakRefSet;
import net.guts.common.ref.WeakRefSet.Performer;

import com.google.inject.Inject;

// Special viewport that must be used in lieu of Viewport (everywhere) in order
// to allow one (or more) empty region.
// This port accepts in its center only views which content implement a special 
// marker interface. Those special views are accepted no where else.
class GutsViewport extends Viewport
{
	private static final long serialVersionUID = 5917540060674540240L;

	@Inject GutsViewport(EmptyableViewportPolicy viewportPolicy)
	{
		_viewportPolicy = viewportPolicy;
		_ports.add(this);
	}

	//TODO LATER move this method somewhere else (GutsViewportDockingStrategy), not static
	static void updateAllPortsStates()
	{
		_logger.debug("updateAllPortsStates()");
		final GutsViewport[] mainPort = {null};
		_ports.perform(new Performer<GutsViewport>()
		{
			@Override public boolean perform(GutsViewport port)
			{
				if (port.getDockedComponent() != null)
				{
					port.updateState();
					if (mainPort[0] == null && port.isRoot())
					{
						_logger.debug(
							"updateAllPortsStates() mainPort = {}", port.getEmptyViewId());
						mainPort[0] = port;
					}
				}
				return true;
			}
		});
		DockingHelper.trace(_logger, mainPort[0]);
		_initDone = true;
	}
	
//	@SuppressWarnings("unchecked") 
//	static void updateAllPortsStates()
//	{
//		// IMPORTANT! Don't replace the following code with
//		// DockingPortTracker.getDockingPorts(): it doesn't work!
//		GutsViewport mainPort = null;
//		Set<String> ids = DockingManager.getDockableIds();
//		for (String id: ids)
//		{
//			GutsViewport port = 
//				(GutsViewport) DockingManager.getDockable(id).getDockingPort();
//			if (port != null)
//			{
//				port.updateState();
//				if (mainPort == null && port.isRoot())
//				{
//					mainPort = port;
//				}
//			}
//		}
//		DockingHelper.trace(mainPort, "");
//		_initDone = true;
//	}
	
	@SuppressWarnings("unchecked") 
	private void updateState()
	{
		//FIXME throw NPE on drop, and then everything's getting out-of-sync!
		// maybe due to even processing BEFORE docking/undocking fully complete????
		Set<View> views = getViewset(0);
		if (!views.isEmpty())
		{
			_logger.debug("updateState() [!views.isEmpty()]");
			View view = views.iterator().next();
			String idView = view.getPersistentId();
			if (_viewportPolicy.isEmptyView(idView))
			{
				_logger.debug("\tupdateState() [isEmptyView()]");
				_emptyViewId = idView;
				setSingleTabAllowed(false);
			}
			else
			{
				_logger.debug("\tupdateState() [!isEmptyView()]");
				_emptyViewId = _viewportPolicy.getTargetViewportEmptyView(view);
				setSingleTabAllowed(views.size() > 1);
			}
		}
		else
		{
			_logger.debug("updateState() [views.isEmpty()]");
			_emptyViewId = null;
			setSingleTabAllowed(false);
		}
	}
	
	String getEmptyViewId()
	{
		return _emptyViewId;
	}
	
	boolean isEmptyablePort()
	{
		return _emptyViewId != null;
	}
	
	private boolean isCenterTargetView(View view)
	{
		if (view.getPersistentId().equals(_emptyViewId))
		{
			return true;
		}
		else
		{
			return equals(_viewportPolicy.getTargetViewportEmptyView(view), _emptyViewId);
		}
	}
	
	static private boolean equals(String a, String b)
	{
		return (a == null ? b == null : a.equals(b));
	}
	
	@Override protected JTabbedPane createTabbedPane()
	{
		Insets oldInsets = UIManager.getInsets(LookAndFeelSettings.TAB_PANE_BORDER_INSETS);
		int tabPlacement = getInitTabPlacement();
		int edgeInset = LookAndFeelSettings.getTabEdgeInset(tabPlacement);
		
		Insets newInsets = new Insets(0, 2, 0, 2);
		switch (tabPlacement)
		{
			case JTabbedPane.LEFT:
			newInsets.left = edgeInset >= 0 ? edgeInset : oldInsets.left;
			break;

			case JTabbedPane.BOTTOM:
			newInsets.bottom = edgeInset >= 0 ? edgeInset : oldInsets.bottom;
			break;

			case JTabbedPane.RIGHT:
			newInsets.right = edgeInset >= 0 ? edgeInset : oldInsets.right;
			break;
			
			case JTabbedPane.TOP:
			default:
			newInsets.top = edgeInset >= 0 ? edgeInset : oldInsets.top;
			break;
		}
		
		UIManager.put(LookAndFeelSettings.TAB_PANE_BORDER_INSETS, newInsets);
		JTabbedPane pane = new JTabbedPane();
		pane.setTabPlacement(tabPlacement);
		UIManager.put(LookAndFeelSettings.TAB_PANE_BORDER_INSETS, oldInsets);
		
		TabbedDragListener tdl = new TabbedDragListener();
		pane.addMouseListener(tdl);
		pane.addMouseMotionListener(tdl);
		
		pane.addChangeListener(ActiveDockableListener.getInstance());

		if (_listener != null)
		{
			// Listen to changes of current selection in tab
			pane.addChangeListener(_listener);
		}
		return pane;
	}

	@Override public boolean isDockingAllowed(Component comp, String region)
	{
		boolean allow = super.isDockingAllowed(comp, region);
		if (allow && _initDone)
		{
			// Special check for emptyable viewports
			if (isEmptyablePort())
			{
				// Docking in emptyable viewport: view must be special target and CENTER
				allow = (isCenterTargetView((View) comp) == CENTER_REGION.equals(region));
			}
		}
		_logger.debug("isDockingAllowed(View(id={}), {}) on port(emptyId={}) => {}",
			new Object[]{((View) comp).getPersistentId(), region, getEmptyViewId(), allow});
		return allow;
	}

	// Fix of a flexdock bug: getViewset(0) actually goes 2 levels (instead of one)
	@SuppressWarnings("unchecked") 
	@Override public Set<View> getViewset(int depth)
	{
		return getDockableSet(depth, 1, View.class);
	}

	void setListener(ChangeListener listener)
	{
		_listener = listener;
	}

	static final private Logger _logger = LoggerFactory.getLogger(GutsViewport.class);
	
	static private boolean _initDone = false;
	// TODO Move later on to GutsViewportDockingStrategy as non-static field...
	static final private WeakRefSet<GutsViewport> _ports = WeakRefSet.create();
	private final EmptyableViewportPolicy _viewportPolicy;
	private String _emptyViewId = null;
	private ChangeListener _listener;
}
