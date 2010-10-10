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

package net.guts.gui.docking;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableBackupFactory;

import com.google.inject.Inject;

class DockingInitializer
{
	@Inject DockingInitializer(
		CControl controller, final ViewFactory viewFactory, DockingSetup setup)
	{
		SingleCDockableBackupFactory backupFactory = new SingleCDockableBackupFactory()
		{
			@Override public SingleCDockable createBackup(String id)
			{
				return viewFactory.createSingle(id);
			}
		};

		// For each registered dockable, register a backup factory
		// (to create it from a saved layout)
		for (String id: setup.listDockables())
		{
			controller.addSingleBackupFactory(id, backupFactory);
		}
		
		// Create each registered working area
		DefaultMultipleCDockableFactory multiFactory = new DefaultMultipleCDockableFactory();
		for (String id: setup.listWorkingAreas())
		{
			controller.createWorkingArea(id);
			controller.addMultipleDockableFactory(id, multiFactory);
		}
		
		// Create each registered content area
		for (String id: setup.listContentAreas())
		{
			if (CControl.CONTENT_AREA_STATIONS_ID.equals(id))
			{
				// This is to make sure that subsequent calls to getContentArea() don't fail
				controller.getContentArea();
			}
			else
			{
				controller.createContentArea(id);
			}
		}
		
		// Create each registered grid area
		for (String id: setup.listGridAreas())
		{
			controller.createGridArea(id);
		}

		// Create each registered minimized area
		for (String id: setup.listMinimizeAreas())
		{
			controller.createMinimizeArea(id);
		}
	}
}
