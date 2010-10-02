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

package net.guts.gui.simple.provider.application;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import net.guts.event.Consumes;
import net.guts.gui.exit.ExitController;
import net.guts.gui.resource.ResourceInjector;
import net.guts.gui.session.SessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class AP_WindowControllerImpl implements AP_WindowController {

	static final private Logger log = LoggerFactory
			.getLogger(AP_WindowControllerImpl.class);

	private Window current;

	final private ResourceInjector resources;

	final private SessionManager sessions;

	@Inject
	AP_WindowControllerImpl(ResourceInjector resources, SessionManager sessions) {

		this.resources = resources;
		this.sessions = sessions;
		this.current = null;

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				if (event instanceof WindowEvent) {
					AP_WindowControllerImpl.this.eventDispatched((WindowEvent) event);
				}
			}
		}, AWTEvent.WINDOW_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);

	}

	private void eventDispatched(WindowEvent event) {

		final Window window = event.getWindow();

		switch (event.getID()) {
		case WindowEvent.WINDOW_ACTIVATED:
			current = window;
			break;

		case WindowEvent.WINDOW_DEACTIVATED:
			current = null;
			break;

		case WindowEvent.COMPONENT_HIDDEN:
		case WindowEvent.WINDOW_CLOSED:
			save(window);
			break;

		default:
			break;
		}

	}

	private Window findWindow(RootPaneContainer root) {
		if (root instanceof Window) {
			return (Window) root;
		}
		if (root instanceof Applet) {
			Applet applet = (Applet) root;
			String name = applet.getName();
			log.debug("name : {}", name);
			Window window = SwingUtilities.getWindowAncestor(applet);
			if (window == null) {
				return null;
			}
			window.setName(name);
			return window;
		}
		log.error("TODO");
		return null;
	}

	@Override
	public Window getActiveWindow() {
		return current;
	}

	private void initBounds(Window window, BoundsPolicy bounds,
			StatePolicy state) {
		if (bounds.mustPack()) {
			window.pack();
		}
		if (bounds.mustCenter()) {
			window.setLocationRelativeTo(getActiveWindow());
		}
		if (state == StatePolicy.RESTORE_IF_EXISTS) {
			load(window);
		}
	}

	private void injectResources(Window window) {
		resources.injectHierarchy(window);
	}

	private void load(Window window) {
		sessions.restore(window);
		log.debug("window : {}", window);
	}

	@Consumes(priority = Integer.MIN_VALUE + 1)
	public void localeChanged(Locale locale) {
		for (Window window : Window.getWindows()) {
			if (window.isVisible()) {
				window.repaint();
				injectResources(window);
				if (window instanceof RootPaneContainer) {
					RootPaneContainer root = (RootPaneContainer) window;
					root.getRootPane().revalidate();
				}
			}
		}
	}

	private void save(Window window) {
		sessions.save(window);
		log.debug("window : {}", window);
	}

	@Override
	public void show(RootPaneContainer root) {

		BoundsPolicy bounds = BoundsPolicy.PACK_AND_CENTER;
		StatePolicy state = StatePolicy.RESTORE_IF_EXISTS;

		show(root, bounds, state);

	}

	@Override
	public void show(RootPaneContainer root, BoundsPolicy bounds,
			StatePolicy state) {

		if (!EventQueue.isDispatchThread()) {
			throw new IllegalStateException(
					"WindowController.show() must be called from the EDT");
		}

		Window window = findWindow(root);

		if (window == null) {
			return;
		}

		injectResources(window);

		initBounds(window, bounds, state);

		window.setVisible(true);

	}

	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE + 1)
	public void shutdown(Void nothing) {

		for (Window window : Window.getWindows()) {
			if (window.isVisible()) {
				save(window);
			}
		}

		log.debug("finished");

	}

}
