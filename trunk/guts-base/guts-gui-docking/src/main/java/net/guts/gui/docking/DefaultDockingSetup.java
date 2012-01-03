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

import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class DefaultDockingSetup implements DockingSetup
{
	@Inject DefaultDockingSetup(
		@BindViewsMap Map<String, Class<? extends JComponent>> views,
		@BindContentAreasSet Set<String> contentAreas,
		@BindWorkingAreasSet Set<String> workingAreas,
		@BindGridAreasSet Set<String> gridAreas,
		@BindMinimizeAreasSet Set<String> minimizeAreas)
	{
		_views = views;
		_contentAreas = contentAreas;
		_workingAreas = workingAreas;
		_gridAreas = gridAreas;
		_minimizeAreas = minimizeAreas;
	}
	
	@Override public Set<String> listDockables()
	{
		return _views.keySet();
	}

	@Override public Class<? extends JComponent> getDockableComponent(String id)
	{
		return _views.get(id);
	}

	@Override public Set<String> listWorkingAreas()
	{
		return _workingAreas;
	}

	@Override public Set<String> listContentAreas()
	{
		return _contentAreas;
	}

	@Override public Set<String> listGridAreas()
	{
		return _gridAreas;
	}

	@Override public Set<String> listMinimizeAreas()
	{
		return _minimizeAreas;
	}

	final private Map<String, Class<? extends JComponent>> _views;
	final private Set<String> _contentAreas;
	final private Set<String> _workingAreas;
	final private Set<String> _gridAreas;
	final private Set<String> _minimizeAreas;
}
