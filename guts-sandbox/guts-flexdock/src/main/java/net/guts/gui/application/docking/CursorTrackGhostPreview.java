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
import java.awt.Polygon;
import java.util.Map;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingPort;
import org.flexdock.docking.drag.effects.DragPreview;
import org.flexdock.docking.drag.preview.GhostPreview;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class CursorTrackGhostPreview extends GhostPreview implements DragPreview
{
	@Inject CursorTrackGhostPreview(CursorTracker tracker)
	{
		_tracker = tracker;
	}
	
	@SuppressWarnings("unchecked") @Override 
	public Polygon createPreviewPolygon(Component dockable, DockingPort port,
		Dockable hover, String targetRegion, Component paintingTarget, Map dragInfo)
	{
		if (!_tracker.trackCursor(dockable, port, targetRegion, paintingTarget))
		{
			return null;
		}
		else
		{
			return super.createPreviewPolygon(
				dockable, port, hover, targetRegion, paintingTarget, dragInfo);
		}
	}

	private final CursorTracker	_tracker;
}
