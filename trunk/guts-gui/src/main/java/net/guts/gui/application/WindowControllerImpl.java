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

package net.guts.gui.application;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.event.Consumes;
import net.guts.gui.exit.ExitController;
import net.guts.gui.resource.ResourceInjector;
import net.guts.gui.session.SessionManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.internal.Nullable;

@Singleton
class WindowControllerImpl implements WindowController
{
	static final private Logger _logger = LoggerFactory.getLogger(WindowControllerImpl.class);
	
	@Inject WindowControllerImpl(ResourceInjector injector, SessionManager sessions,
		@Nullable JApplet applet)
	{
		_injector = injector;
		_sessions = sessions;
		_applet = applet;
		_current = null;
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
		{
			public void eventDispatched(AWTEvent event)
			{
				if (event instanceof WindowEvent)
				{
					WindowControllerImpl.this.eventDispatched((WindowEvent) event);
				}
			}
		}, AWTEvent.WINDOW_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
	}
	
	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE + 1)
	public void shutdown(Void nothing)
	{
		_logger.debug("shutdown()");
		// Find all visible windows and save session for each
		for (Window window: Window.getWindows())
		{
			if (window.isVisible())
			{
				saveState(window);
			}
		}
		// Special code for applets (not listed in Window.getWindows())
		if (_applet != null)
		{
			saveState(_applet);
		}
	}
	
	@Consumes(priority = Integer.MIN_VALUE + 1)
	public void localeChanged(Locale locale)
	{
		for (Window window: Window.getWindows())
		{
			if (window.isVisible())
			{
				injectResources(window);
				window.repaint();
				if (window instanceof RootPaneContainer)
				{
					((RootPaneContainer) window).getRootPane().revalidate();
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.WindowController#getActiveWindow()
	 */
	@Override public Window getActiveWindow()
	{
		return _current;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.WindowController#show(javax.swing.JFrame)
	 */
	@Override public void show(JFrame frame, BoundsPolicy bounds, StatePolicy state)
	{
		showWindow(frame, bounds, state);
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.WindowController#show(javax.swing.JDialog)
	 */
	@Override public void show(JDialog dialog, BoundsPolicy bounds, StatePolicy state)
	{
		showWindow(dialog, bounds, state);
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.application.WindowController#showApplet(boolean)
	 */
	@Override public void showApplet(StatePolicy state)
	{
		if (!EventQueue.isDispatchThread())
		{
			throw new IllegalStateException(
				"WindowController.show() must be called from the EDT!");
		}
		if (_applet != null)
		{
			_injector.injectHierarchy(_applet);
			_applet.setSize(_applet.getLayout().preferredLayoutSize(_applet));
			if (state == StatePolicy.RESTORE_IF_EXISTS)
			{
				// Restore size from session storage if any
				_sessions.restore(_applet);
			}
			// Initialize current active window
			_current = SwingUtilities.getWindowAncestor(_applet);
		}
	}

	private void showWindow(Window container, BoundsPolicy bounds, StatePolicy state)
	{
		if (!EventQueue.isDispatchThread())
		{
			throw new IllegalStateException(
				"WindowController.show() must be called from the EDT!");
		}
		// Perform i18n if not done already
		injectResources(container);
		// Calculate size (based on existing session state)
		initBounds(container, bounds, state);
		container.setVisible(true);
	}
	
	private void injectResources(Window container)
	{
		_injector.injectHierarchy(container);
	}
	
	private void initBounds(Window container, BoundsPolicy bounds, StatePolicy state)
	{
		// Initialize location and size according to policy
		if (bounds.mustPack())
		{
			container.pack();
		}
		if (bounds.mustCenter())
		{
			container.setLocationRelativeTo(getActiveWindow());
		}

		if (state == StatePolicy.RESTORE_IF_EXISTS)
		{
			// Restore size from session storage if any
			_sessions.restore(container);
		}
	}
	
	private void saveState(Component container)
	{
		_sessions.save(container);
	}
	
	private void eventDispatched(WindowEvent event)
	{
		switch (event.getID())
		{
			case WindowEvent.WINDOW_ACTIVATED:
			_current = event.getWindow();
			break;

			case WindowEvent.WINDOW_DEACTIVATED:
			_current = null;
			break;

			case WindowEvent.COMPONENT_HIDDEN:
			case WindowEvent.WINDOW_CLOSED:
			_logger.debug("WINDOW_CLOSED");
			// Save window state in session storage
			saveState(event.getWindow());
			break;
				
			default:
			// Do nothing (we are only interested in the above events)
			break;
		}
	}

	private Window _current;
	final private ResourceInjector _injector;
	final private SessionManager _sessions;
	final private JApplet _applet;
}
