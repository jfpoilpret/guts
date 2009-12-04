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

package net.guts.gui.session;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import net.guts.gui.util.ScreenTools;

// Any actual component state should be a bean that implements this interface
public interface SessionState<T extends Component>
{
	public void reset();
	public void extractState(T component);
	public void injectState(T component);
}

//TODO later on, integrate multi-screen problems?
// - when restoring state check that bounds are visible on the current real estate 
// (one whole rectangle), if not, then don't restore state at all
// - or just check if real estate has changed between store and restore times?
//TODO refactor because WindowState and FrameState will share a lot of code!
class WindowState implements SessionState<Window>
{
	@Override public void reset()
	{
		_bounds = null;
	}
	
	@Override public void extractState(Window component)
	{
		_bounds = component.getBounds();
	}

	@Override public void injectState(Window component)
	{
		if (_bounds != null)
		{
			component.setBounds(_bounds);
		}
	}
	
	private Rectangle _bounds = null;
}

class FrameState implements SessionState<JFrame>
{
	@Override public void reset()
	{
		_bounds = null;
		_state = JFrame.NORMAL;
		_screensEstate = null;
	}
	
	@Override public void extractState(JFrame component)
	{
		_bounds = (Rectangle) component.getRootPane().getClientProperty(SAVED_BOUNDS);
		if (_bounds == null)
		{
			_bounds = component.getBounds();
		}
		_state = component.getExtendedState();
		_screensEstate = ScreenTools.getScreenEstate();
	}

	@Override public void injectState(JFrame component)
	{
		// Register resize listener if JFrame
		component.addComponentListener(_listener);
		if (checkBounds())
		{
			component.setBounds(_bounds);
			component.setExtendedState(_state);
		}
	}
	
	// Check if screen real estate has changed since previous session
	private boolean checkBounds()
	{
		return		_bounds != null
				&&	Arrays.equals(_screensEstate, ScreenTools.getScreenEstate());
	}
	
	static private void frameBoundsChanged(ComponentEvent event)
	{
		((JFrame) event.getComponent()).getRootPane().putClientProperty(
			SAVED_BOUNDS, event.getComponent().getBounds());
	}
	
	static final private ComponentListener _listener = new ComponentAdapter()
	{
		@Override public void componentResized(ComponentEvent event)
		{
			frameBoundsChanged(event);
		}
		
		@Override public void componentMoved(ComponentEvent event)
		{
			frameBoundsChanged(event);
		}
	};
	
	static final private String SAVED_BOUNDS = 
		WindowState.class.getCanonicalName() + ".SavedBounds";
	private Rectangle[] _screensEstate = null;
	private Rectangle _bounds = null;
	private int _state = JFrame.NORMAL;
}

class SplitPaneState implements SessionState<JSplitPane>
{
	@Override public void reset()
	{
		_divider = -1;
	}
	
	@Override public void extractState(JSplitPane component)
	{
		_divider = component.getDividerLocation();
	}

	@Override public void injectState(JSplitPane component)
	{
		if (_divider > -1)
		{
			component.setDividerLocation(_divider);
		}
	}
	
	private int _divider = -1;
}

class TableState implements SessionState<JTable>
{
	@Override public void reset()
	{
		_columns = null;
	}
	
	@Override public void extractState(JTable component)
	{
		TableColumnModel model = component.getColumnModel();
		int size = model.getColumnCount();
		_columns = new int[size];
		for (int i = 0; i < size; i++)
		{
			_columns[i] = model.getColumn(i).getWidth();
		}
	}

	@Override public void injectState(JTable component)
	{
		if (_columns != null)
		{
			TableColumnModel model = component.getColumnModel();
			int size = Math.min(model.getColumnCount(), _columns.length);
			for (int i = 0; i < size; i++)
			{
				model.getColumn(i).setPreferredWidth(_columns[i]);
			}
		}
	}
	
	private int[] _columns = null;
}

class TabbedPaneState implements SessionState<JTabbedPane>
{
	@Override public void reset()
	{
		_lastSelectedTab = -1;
	}
	
	@Override public void extractState(JTabbedPane component)
	{
		_lastSelectedTab = component.getSelectedIndex();
	}

	@Override public void injectState(JTabbedPane component)
	{
		if (_lastSelectedTab > -1 && _lastSelectedTab < component.getTabCount())
		{
			component.setSelectedIndex(_lastSelectedTab);
		}
	}
	
	private int _lastSelectedTab = -1;
}

