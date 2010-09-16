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

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockableFactory;

import com.google.inject.Inject;

class GutsDockableFactory extends DockableFactory.Stub
{
	@Inject GutsDockableFactory(EmptyViewsRegistry emptyViewsRegistry,
		ViewContentFactory contentFactory, ViewFactory viewFactory)
	{
		_emptyViewsRegistry = emptyViewsRegistry;
		_contentFactory = contentFactory;
		_viewFactory = viewFactory;
	}
	
	@Override public Dockable getDockable(String id)
	{
		Dockable dockable = _emptyViewsRegistry.getEmptyView(id);
		return (dockable != null ? dockable : createDockable(id));
	}
	
	protected Dockable createDockable(String id)
	{
		// Instantiate actual component
		JComponent content = _contentFactory.createContent(id);
		// Instantiate dockable view
		return _viewFactory.createView(id, content);
	}
	
	private final EmptyViewsRegistry _emptyViewsRegistry;
	private final ViewContentFactory _contentFactory;
	private final ViewFactory _viewFactory;
}
