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

import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.activation.ActiveDockableListener;
import org.flexdock.docking.event.TabbedDragListener;
import org.flexdock.util.LookAndFeelSettings;
import org.flexdock.view.View;
import org.flexdock.view.Viewport;

import com.google.inject.Inject;

//TODO: find a better way to set which View should use the empty viewport?
// -> avoid keeping public API...
// - also remove EmptyableViewMarker!

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
	}
	
	@Override public boolean dock(Dockable dockable, String region)
	{
		boolean result = super.dock(dockable, region);
		if (_initDone)
		{
			if (result && _viewportPolicy.isEmptyView(dockable.getPersistentId()))
			{
				_emptyViewId = dockable.getPersistentId();
			}
			_initDone = true;
		}
		return result;
	}

	String getEmptyViewId()
	{
		return _emptyViewId;
	}
	
	boolean isEmptyablePort()
	{
		return _emptyViewId != null;
	}
	
	private boolean isMarkedView(View view)
	{
		return _viewportPolicy.isTargetForEmptyableViewport(_emptyViewId, view);
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
			// Special check for board drawing areas
			allow = isEmptyablePort() && CENTER_REGION.equals(region);
			if (!isMarkedView((View) comp))
			{
				allow = !allow;
			}
		}
		return allow;
	}

	void setListener(ChangeListener listener)
	{
		_listener = listener;
	}
	
	private final EmptyableViewportPolicy _viewportPolicy;
	private String _emptyViewId = null;
	private boolean _initDone = false;
	private ChangeListener _listener;
}