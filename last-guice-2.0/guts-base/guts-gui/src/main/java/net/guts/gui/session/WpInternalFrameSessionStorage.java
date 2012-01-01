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
import java.awt.Window;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.guts.event.Consumes;
import net.guts.gui.exit.ExitController;
import net.guts.gui.window.AbstractWindowProcessor;
import net.guts.gui.window.RootPaneConfig;
import net.guts.gui.window.StatePolicy;

import com.google.inject.Inject;

class WpInternalFrameSessionStorage 
extends AbstractWindowProcessor<JInternalFrame, JInternalFrame>
{
	@Inject WpInternalFrameSessionStorage(SessionManager sessions)
	{
		super(JInternalFrame.class, JInternalFrame.class);
		_sessions = sessions;
	}

	@Override protected void processRoot(
		JInternalFrame root, RootPaneConfig<JInternalFrame> config)
	{
		StatePolicy state = config.get(StatePolicy.class);
		if (state == StatePolicy.RESTORE_IF_EXISTS)
		{
			// Restore size from session storage if any
			_sessions.restore(root);
		}
		root.addInternalFrameListener(_listener);
	}
	
	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE)
	public void shutdown(Void nothing)
	{
		// Find all visible windows and save session for each
		for (Window window: Window.getWindows())
		{
			if (window.isVisible() && window instanceof JFrame)
			{
				JFrame frame = (JFrame) window;
				if (frame.getContentPane() instanceof JDesktopPane)
				{
					JDesktopPane desktop =  (JDesktopPane) frame.getContentPane();
					// Find all visible internal frames and save session for each
					for (JInternalFrame child: desktop.getAllFrames())
					{
						saveState(child);
					}
				}
			}
		}
	}
	
	private void saveState(Component container)
	{
		_sessions.save(container);
	}
	
	final private InternalFrameListener _listener = new InternalFrameAdapter()
	{
		@Override public void internalFrameClosed(InternalFrameEvent e)
		{
			saveState(e.getInternalFrame());
		}
	};
	final private SessionManager _sessions;
}
