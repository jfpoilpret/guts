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

package net.guts.gui.application.support;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.event.Consumes;
import net.guts.gui.exit.ExitController;
import net.guts.gui.resource.ResourceInjector;
import net.guts.gui.session.SessionManager;
import net.guts.gui.window.BoundsPolicy;
import net.guts.gui.window.StatePolicy;

import com.google.inject.Inject;
import com.google.inject.Singleton;

//TODO refactor all MDI to its own package? (eg application.support.mdi)
@Singleton
class MDIFrameControllerImpl implements MDIFrameController
{
	static final private Logger _logger = 
		LoggerFactory.getLogger(MDIFrameControllerImpl.class);

	@Inject MDIFrameControllerImpl(ResourceInjector injector, SessionManager sessions)
	{
		_injector = injector;
		_sessions = sessions;
	}
	
	@Override public void initFrame(JFrame frame)
	{
		frame.setContentPane(_desktop);
	}
	
	@Override public void show(JInternalFrame frame, BoundsPolicy bounds, StatePolicy state)
	{
		// Listen to internal frame closure , in order to save session
		frame.addInternalFrameListener(_listener);
		// Inject resources into new frame
		_injector.injectHierarchy(frame);
		// Initialize location and size according to policy
		if (bounds.mustPack())
		{
			frame.pack();
		}
		if (bounds.mustCenter())
		{
			// Calculate the coordinates in order to center the frame
			int w1 = _desktop.getWidth();
			int w2 = frame.getWidth();
			int x = Math.max((w2 - w1) / 2, 0);
			int h1 = _desktop.getHeight();
			int h2 = frame.getHeight();
			int y = Math.max((h2 - h1) / 2, 0);
			frame.setLocation(x, y);
		}

		if (state == StatePolicy.RESTORE_IF_EXISTS)
		{
			// Restore geometry from session storage if any
			_sessions.restore(frame);
		}
		_desktop.add(frame);
		frame.show();
	}
	
	@Override public JInternalFrame getActiveFrame()
	{
		return _desktop.getSelectedFrame();
	}

	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE)
	public void shutdown(Void nothing)
	{
		_logger.debug("shutdown()");
		// Find all visible internal frames and save session for each
		for (JInternalFrame frame: _desktop.getAllFrames())
		{
			saveState(frame);
		}
	}
	
	private void saveState(JInternalFrame frame)
	{
		_sessions.save(frame);
	}

	final private InternalFrameListener _listener = new InternalFrameAdapter()
	{
		@Override public void internalFrameClosed(InternalFrameEvent e)
		{
			saveState(e.getInternalFrame());
		}
	};

	final private JDesktopPane _desktop = new JDesktopPane();
	final private ResourceInjector _injector;
	final private SessionManager _sessions;
}
