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

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.flexdock.docking.DockableFactory;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.docking.drag.effects.DragPreview;
import org.flexdock.event.EventManager;
import org.flexdock.perspective.PerspectiveFactory;
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.perspective.persist.PersistenceHandler;
import org.flexdock.view.Viewport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.event.Consumes;
import net.guts.gui.application.support.SingleFrameLifecycle;
import net.guts.gui.exit.ExitController;

import com.google.inject.Inject;

//CSOFF: AbstractClassNameCheck
abstract public class DockingLifecycle extends SingleFrameLifecycle
{
	static final private Logger _logger = LoggerFactory.getLogger(DockingLifecycle.class);

	/* (non-Javadoc)
	 * @see net.guts.gui.application.support.SingleFrameLifecycle#initFrame(javax.swing.JFrame)
	 */
	@Override final protected void initFrame(JFrame mainFrame)
	{
		Viewport mainPort = _portFactory.createViewport();
		// Initialize flexdock
		loadLayout();
		// Finalize initialization of all created viewports once layout has been created
		GutsViewport.completeInitialization();
		// Fix for Flexdock bug if drop on forbidden port
		EventManager.addListener(new GutsDockingListener());
		mainFrame.add(mainPort);
		// Initialize main frame
		initMainFrame(mainFrame);
	}
	
	@Inject void init(DockableFactory dockableFactory, DockingStrategy strategy, 
		PerspectiveFactory perspectiveFactory, PersistenceHandler persistenceHandler,
		DragPreview dragPreview, ViewportFactory portFactory)
	{
		_portFactory = portFactory;
		DockingManager.setDockableFactory(dockableFactory);
		// make sure the right DockingStrategy is used for GutsViewport
		DockingManager.setDockingStrategy(GutsViewport.class, strategy);
		// make sure the right DockingStrategy is used for any Dockable
		DockingManager.setDockingStrategy(JComponent.class, strategy);
		DockingManager.setDragPreview(dragPreview);
		//TODO: add support for floating and minimization
		DockingManager.setFloatingEnabled(false);
		DockingManager.setSingleTabsAllowed(_allowSingleTabs);
		DockingManager.setAutoPersist(false);
		
		PerspectiveManager.setFactory(perspectiveFactory);
		PerspectiveManager.setPersistenceHandler(persistenceHandler);
		PerspectiveManager.getInstance().setCurrentPerspective(getDefaultPerspectiveId(), true);
	}
	
	protected String getDefaultPerspectiveId()
	{
		return Docking.DEFAULT_PERSPECTIVE;
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
			// problem with session previously stored!
			_logger.error("loadLayout", e);
		}
		DockingManager.restoreLayout();
	}
	// CSON: IllegalCatchCheck
	
	abstract protected void	initMainFrame(JFrame frame);

	@Consumes(topic = ExitController.SHUTDOWN_EVENT, priority = Integer.MIN_VALUE)
	public void shutdown(Void nothing)
	{
		//TODO remove once sure it works without a public method, but it doesn't work yet...
		_logger.debug("shutdown()");
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

	//TODO add special injection for that? Use a bound DockingConfig class?
	public void setAllowSingleTabs(boolean allowSingleTabs)
	{
		_allowSingleTabs = allowSingleTabs;
	}

	private ViewportFactory _portFactory;
	private boolean _allowSingleTabs;
}
//CSON: AbstractClassNameCheck
