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
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.guts.gui.util.ScreenTools;

/**
 * Interface allowing any kind of {@code java.awt.Component} to have its state
 * (geometry information, as set manually set by the end-user) saved and
 * restored across several launches of the application.
 * <p/>
 * Any actual component state should be a class that implements this interface.
 * This class will be serialized by {@link SerializationManager}.
 * <p/>
 * {@code SessionState<T>} implementations are used as singletons by 
 * {@link SessionManager}, i.e. they are reused for all components of the same type.
 * <p/>
 * Guts-GUI allows defining one implementation of {@code SessionState<T>} per
 * class of {@code Component}. You can register your own implementations with
 * {@link Sessions#bindSessionConverter}.
 * <p/>
 * Guts-GUI provides implementations for the following:
 * <ul>
 * <li>{@link java.awt.Window}</li>
 * <li>{@link javax.swing.JFrame}</li>
 * <li>{@link javax.swing.JTabbedPane}</li>
 * <li>{@link javax.swing.JTable}</li>
 * <li>{@link javax.swing.JSplitPane}</li>
 * </ul>
 * More information on these implementations can be found in 
 * <a href="package-summary.html#session3">net.guts.gui.session package description</a>.
 * 
 * @param <T> type of the {@link java.awt.Component} handled by this {@code SessionState}
 *
 * @author Jean-Francois Poilpret
 */
public interface SessionState<T extends Component>
{
	/**
	 * Called before fields of {@code this} instance are populated from the 
	 * previously stored state.
	 * {@code reset()} should initialize all fields to their default values,
	 * thus allowing to populate only some fields from the currently stored
	 * state (can be useful when the fields change across versions).
	 * <p/>
	 * This method is needed because {@code SessionState<T>} implementations are
	 * reused for all components of type {@code T}.
	 */
	public void reset();
	
	/**
	 * Called by {@link SessionManager#save(Component)}, this method must extract,
	 * from {@code component}, the state to be persisted. That state must be stored
	 * in fields of {@code this} instance.
	 * <p/>
	 * This method must deal with {@code component} state exclusively, not the state
	 * of any child component, because {@link SessionManager#save(Component)} directly
	 * handles state persistence of {@code component} hierarchy, and finds the right
	 * {@code SessionState<T>} implementation for each child.
	 * 
	 * @param component the component which state must be extracted and stored in 
	 * fields of {@code this} instance
	 */
	public void extractState(T component);
	
	/**
	 * Called by {@link SessionManager#restore(Component)} after it has populated 
	 * fields of {@code this} instance from previously persisted state of 
	 * {@code component}, this method must inject values from its fields into
	 * {@code component}.
	 * <p/>
	 * This method must deal with {@code component} state exclusively, not the state
	 * of any child component, because {@link SessionManager#restore(Component)} directly
	 * handles state persistence of {@code component} hierarchy, and finds the right
	 * {@code SessionState<T>} implementation for each child.
	 * 
	 * @param component the component which state must be injected from fields of 
	 * {@code this} instance
	 */
	public void injectState(T component);
}

// Class wrapping current screen estate with bounds of a given window
class ScreenEstate
{
	// Default constructor for deserialization
	ScreenEstate() {}

	ScreenEstate(Rectangle bounds)
	{
		_bounds = bounds;
		_screensEstate = ScreenTools.getScreenEstate();
	}

	Rectangle bounds()
	{
		// Check if screen real estate has changed since previous session
		if (	_bounds != null
			&&	Arrays.equals(_screensEstate, ScreenTools.getScreenEstate()))
		{
			return _bounds;
		}
		else
		{
			return null;
		}
	}
	
	private Rectangle[] _screensEstate;
	private Rectangle _bounds;
}

class WindowState implements SessionState<Window>
{
	@Override public void reset()
	{
		_estate = null;
	}
	
	@Override public void extractState(Window component)
	{
		_estate = new ScreenEstate(component.getBounds());
	}

	@Override public void injectState(Window component)
	{
		Rectangle bounds = (_estate != null ? _estate.bounds() : null);
		if (bounds != null)
		{
			component.setBounds(bounds);
		}
	}
	
	private ScreenEstate _estate = null;
}

class FrameState implements SessionState<JFrame>
{
	@Override public void reset()
	{
		_state = JFrame.NORMAL;
		_estate = null;
	}
	
	@Override public void extractState(JFrame component)
	{
		_state = component.getExtendedState();
		_estate = new ScreenEstate(getBounds(component));
	}

	@Override public void injectState(JFrame component)
	{
		// Register resize listener if JFrame
		component.addComponentListener(_listener);
		Rectangle bounds = (_estate != null ? _estate.bounds() : null);
		if (bounds != null)
		{
			component.setBounds(bounds);
			saveBounds(component);
			component.setExtendedState(_state);
		}
	}

	static private Rectangle getBounds(JFrame frame)
	{
		Rectangle bounds = 
			(Rectangle) frame.getRootPane().getClientProperty(SAVED_BOUNDS);
		if (bounds != null)
		{
			return bounds;
		}
		else
		{
			return frame.getBounds();
		}
	}
	
	static private void saveBounds(JFrame frame)
	{
		// Save bounds only when frame is in normal state (not maximized, minimized...)
		if (frame.getExtendedState() == JFrame.NORMAL)
		{
			frame.getRootPane().putClientProperty(SAVED_BOUNDS, frame.getBounds());
		}
	}
	
	static final private ComponentListener _listener = new ComponentAdapter()
	{
		@Override public void componentResized(ComponentEvent event)
		{
			saveBounds((JFrame) event.getComponent());
		}
		
		@Override public void componentMoved(ComponentEvent event)
		{
			saveBounds((JFrame) event.getComponent());
		}
	};
	
	static final private String SAVED_BOUNDS = 
		FrameState.class.getCanonicalName() + ".SavedBounds";
	private ScreenEstate _estate = null;
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
		_columnWidths = null;
		_columnIndexes = null;
	}
	
	@Override public void extractState(JTable component)
	{
		TableColumnModel model = component.getColumnModel();
		int size = model.getColumnCount();
		_columnWidths = new int[size];
		_columnIndexes = new int[size];
		for (int i = 0; i < size; i++)
		{
			_columnWidths[i] = model.getColumn(i).getWidth();
			_columnIndexes[i] = component.convertColumnIndexToView(i);
		}
	}

	@Override public void injectState(JTable component)
	{
		if (_columnWidths != null)
		{
			TableColumnModel model = component.getColumnModel();
			int size = _columnWidths.length;
			if (size == model.getColumnCount())
			{
				// Reorder the columns according to the previously saved state
				// And also set preferred size according to stored state
				TableColumn[] columns = new TableColumn[size];
				for (int i = 0; i < size; i++)
				{
					columns[i] = model.getColumn(_columnIndexes[i]);
					columns[i].setPreferredWidth(_columnWidths[i]);
				}
				// For that, remove all columns and add previous columns again
				for (int i = 0; i < size; i++)
				{
					model.removeColumn(columns[i]);
				}
				for (int i = 0; i < size; i++)
				{
					model.addColumn(columns[i]);
				}
			}
		}
	}
	
	private int[] _columnWidths = null;
	private int[] _columnIndexes = null;
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

