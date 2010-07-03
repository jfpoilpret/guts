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

import java.awt.Component;
import java.awt.Cursor;
import java.util.HashMap;
import java.util.Map;

import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingPort;

import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class CursorTracker implements DockingConstants
{
	@Inject CursorTracker(ResourceInjector injector)
	{
		RegionCursors cursors = new RegionCursors();
		injector.injectInstance(cursors);
		// Create cursors
		_none = cursors.regionNone;
		_regionCursor.put(NORTH_REGION, cursors.regionNorth);
		_regionCursor.put(SOUTH_REGION, cursors.regionSouth);
		_regionCursor.put(WEST_REGION, cursors.regionWest);
		_regionCursor.put(EAST_REGION, cursors.regionEast);
		_regionCursor.put(CENTER_REGION, cursors.regionCenter);
	}
	
	public boolean trackCursor(	Component	dockable, 
								DockingPort	port, 
								String		targetRegion, 
								Component	paintingTarget)
	{
		if (UNKNOWN_REGION.equals(targetRegion))
		{
			return false;
		}
		if (!port.isDockingAllowed(dockable, targetRegion))
		{
			paintingTarget.setCursor(_none);
			return false;
		}
		paintingTarget.setCursor(_regionCursor.get(targetRegion));
		return true;
	}

	//CSOFF: MemberNameCheck
	//CSOFF: VisibilityModifierCheck
	static class RegionCursors
	{
		Cursor regionNone;
		Cursor regionNorth;
		Cursor regionSouth;
		Cursor regionWest;
		Cursor regionEast;
		Cursor regionCenter;
	}
	//CSON: VisibilityModifierCheck
	//CSON: MemberNameCheck

	final private Cursor _none;
	final private Map<String, Cursor> _regionCursor = new HashMap<String, Cursor>();
}
