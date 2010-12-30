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

package net.guts.gui.window;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.RootPaneContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.event.Consumes;
import net.guts.gui.exit.ExitController;
import net.guts.gui.session.SessionManager;

import com.google.inject.Inject;

//TODO: add other SessionProcessor for JApplet and JInternalFrame?

// Processor of session restoration and storage for real Window instances
// only (JFrame, JDialog, JWindow)
class WpWindowSessionStorage<V extends RootPaneContainer> 
extends AbstractWindowProcessor<Window, V>
{
	static final private Logger _logger = 
		LoggerFactory.getLogger(WpWindowSessionStorage.class);

	@Inject WpWindowSessionStorage(SessionManager sessions)
	{
		super(Window.class);
		_sessions = sessions;
	}

	@Override protected void processRoot(Window root, RootPaneConfig<V> config)
	{
		StatePolicy state = config._properties.get(StatePolicy.class);
		if (state == StatePolicy.RESTORE_IF_EXISTS)
		{
			// Restore size from session storage if any
			_sessions.restore(root);
		}
		root.addWindowListener(_listener);
		root.addComponentListener(_listener);
	}
	
	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE + 1)
	public void shutdown(Void nothing)
	{
		_logger.debug("shutdown()");
		// Find all visible windows and save session for each
		for (Window window: Window.getWindows())
		{
			if (window.isVisible() && _processedClass.isInstance(window))
			{
				saveState(window);
			}
		}
	}
	
	private void saveState(Component container)
	{
		_sessions.save(container);
	}
	
	private class WindowSessionListener extends WindowAdapter
		implements ComponentListener
	{
		@Override public void windowClosed(WindowEvent event)
		{
			saveState(event.getWindow());
		}

		@Override public void componentHidden(ComponentEvent event)
		{
			saveState(event.getComponent());
		}

		@Override public void componentMoved(ComponentEvent event) {}
		@Override public void componentResized(ComponentEvent event) {}
		@Override public void componentShown(ComponentEvent event) {}
	}
	
	final private SessionManager _sessions;
	final private WindowSessionListener _listener = new WindowSessionListener();
}
