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
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import org.jdesktop.application.ApplicationContext;

import net.guts.event.Consumes;
import net.guts.gui.exit.ExitController;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class WindowControllerImpl implements WindowController
{
	WindowControllerImpl()
	{
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
	
	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE)
	public void shutdown(Void nothing)
	{
		System.out.printf("shutdown()\n");
		// Find all visible windows and save session for each
		for (Window window: Window.getWindows())
		{
			if (	window.isVisible()
				&&	window instanceof RootPaneContainer)
			{
				saveSize((RootPaneContainer) window);
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
	@Override public void show(JFrame frame, BoundsPolicy policy)
	{
		showWindow(frame, policy);
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.WindowController#show(javax.swing.JDialog)
	 */
	@Override public void show(JDialog dialog, BoundsPolicy policy)
	{
		showWindow(dialog, policy);
	}

	private <T extends Window & RootPaneContainer> void showWindow(
		T container, BoundsPolicy policy)
	{
		if (!EventQueue.isDispatchThread())
		{
			throw new IllegalStateException(
				"WindowController.show() must be called from the EDT!");
		}
		// Perform i18n if not done already
		injectResources(container);
		// Calculate size (based on existing session state)
		initBounds(container, policy);
		container.getRootPane().getParent().setVisible(true);
	}
	
	private void injectResources(RootPaneContainer container)
	{
		// First check if resources have already been injected
		JComponent root = container.getRootPane();
		if (root.getClientProperty(RESOURCES_INJECTED) == null)
		{
			_context.getResourceMap().injectComponents(root.getParent());
			root.putClientProperty(RESOURCES_INJECTED, Boolean.TRUE);
		}
	}
	
	private <T extends Window & RootPaneContainer> void initBounds(
		T container, BoundsPolicy policy)
	{
		try
		{
			JComponent root = container.getRootPane();

			// Initialize location and size according to policy
			if (policy == BoundsPolicy.PACK_ONLY || policy == BoundsPolicy.PACK_AND_CENTER)
			{
				container.pack();
			}
			if (policy == BoundsPolicy.CENTER_ONLY || policy == BoundsPolicy.PACK_AND_CENTER)
			{
				container.setLocationRelativeTo(getActiveWindow());
			}
			
			// Restore size from session storage if any
			_context.getSessionStorage().restore(root.getParent(), sessionFileName(container));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void saveSize(RootPaneContainer container)
	{
		try
		{
			JComponent root = container.getRootPane();
			_context.getSessionStorage().save(root.getParent(), sessionFileName(container));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private String sessionFileName(RootPaneContainer container)
	{
		String name = container.getRootPane().getParent().getName();
		return (name == null ? "" : name + ".session.xml");
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

			case WindowEvent.COMPONENT_RESIZED:
			// Save window bounds (as client property?)
			if (event.getWindow() instanceof JFrame)
			{
				System.out.printf("WINDOW_RESIZED\n");
				JFrame frame = (JFrame) event.getWindow();
				frame.getRootPane().putClientProperty(SAVED_BOUNDS, frame.getBounds());
			}
			break;
			
			case WindowEvent.COMPONENT_HIDDEN:
			case WindowEvent.WINDOW_CLOSED:
			System.out.printf("WINDOW_CLOSED\n");
			// Save window state in session storage
			saveSize((RootPaneContainer) event.getWindow());
			break;
				
			default:
			// Do nothing (we are only interested in the above events)
			break;
		}
	}

	private static final String RESOURCES_INJECTED = 
		WindowController.class.getCanonicalName() + ".ResourcesInjected";
	private static final String SAVED_BOUNDS = "WindowState.normalBounds";
//TODO add after rewriting SessionStorage
//		WindowController.class.getCanonicalName() + ".SavedBounds";

	//TODO replace with RootPaneContainer exclusively?
	private Window _current;
	@Inject private ApplicationContext _context;
}
