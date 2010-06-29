//  Copyright 2004-2007 Jean-Francois Poilpret
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

import java.util.Map;

import javax.swing.JFrame;

import org.flexdock.docking.DockableFactory;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.docking.drag.effects.DragPreview;
import org.flexdock.event.EventManager;
import org.flexdock.perspective.PerspectiveFactory;
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.perspective.persist.FilePersistenceHandler;
import org.flexdock.view.Viewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.event.Consumes;
import net.guts.gui.application.docking.EmptyableViewport;
import net.guts.gui.application.docking.ViewportFactory;
import net.guts.gui.application.support.SingleFrameLifecycle;
import net.guts.gui.exit.ExitController;

import com.google.inject.Inject;

//TODO replace persistence in order to use SessionManager!
abstract public class DockingLifecycle extends SingleFrameLifecycle
{
	static final public String	DEFAULT_PERSPECTIVE = "DEFAULT_PERSPECTIVE";

	static final private Logger _logger = LoggerFactory.getLogger(DockingLifecycle.class);

	/* (non-Javadoc)
	 * @see net.guts.gui.application.support.SingleFrameLifecycle#initFrame(javax.swing.JFrame)
	 */
	@Override final protected void initFrame(JFrame mainFrame)
	{
		Viewport mainPort = _portFactory.createViewport();
		// Initialize flexdock
		loadLayout();
		// Fix for Flexdock bug if drop on forbidden port
		EventManager.addListener(new GutsDockingListener());
		mainFrame.add(mainPort);
		// Now we can set actual function of EmptyableViewport
		if (mainPort instanceof EmptyableViewport)
		{
			EmptyableViewport.setInitDone();
		}
		// Initialize main frame
		initMainFrame(mainFrame);
	}
	
	@Inject void init(DockableFactory dockableFactory, DragPreview dragPreview,
		PerspectiveFactory perspectiveFactory, Map<Class<?>, DockingStrategy> strategies,
		ViewportFactory portFactory)
	{
		_portFactory = portFactory;
		DockingManager.setDockableFactory(dockableFactory);
		for (Map.Entry<Class<?>, DockingStrategy> strategy: strategies.entrySet())
		{
			DockingManager.setDockingStrategy(strategy.getKey(), strategy.getValue());
		}
		DockingManager.setDragPreview(dragPreview);
		//TODO: add support for floating and minimization
		DockingManager.setFloatingEnabled(false);
		DockingManager.setSingleTabsAllowed(_allowSingleTabs);
		DockingManager.setAutoPersist(false);
		
		PerspectiveManager.setFactory(perspectiveFactory);
		String dockingFile = System.getProperty("user.home") + "/" + getDockingStateFile();
		PerspectiveManager.setPersistenceHandler(new FilePersistenceHandler(dockingFile));
		PerspectiveManager.getInstance().setCurrentPerspective(getDefaultPerspectiveId(), true);
	}
	
	protected String getDefaultPerspectiveId()
	{
		return DEFAULT_PERSPECTIVE;
	}

	// CSOFF: IllegalCatchCheck
	protected void loadLayout()
	{
		try
		{
			//#### Should check return
			DockingManager.loadLayoutModel();
		}
		catch (Exception e)
		{
			//#### Try to recreate perspective from scratch (in case of a 
			// problem with docking.xml file
			_logger.error("loadLayout", e);
		}
		DockingManager.restoreLayout();
	}
	// CSON: IllegalCatchCheck
	
	abstract protected void	initMainFrame(JFrame frame);

	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE)
	public void shutdown(Void nothing)
	{
		saveLayout();
	}
	
	// CSOFF: IllegalCatchCheck
	protected void saveLayout()
	{
		try
		{
			DockingManager.storeLayoutModel();
		}
		catch (Exception e)
		{
			_logger.error("saveLayout", e);
		}
	}
	// CSON: IllegalCatchCheck

	protected String getDockingStateFile()
	{
		return "docking.xml";
	}
	
	public void setAllowSingleTabs(boolean allowSingleTabs)
	{
		_allowSingleTabs = allowSingleTabs;
	}

	private ViewportFactory _portFactory;
	private boolean _allowSingleTabs;
}
