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

import org.flexdock.docking.DockableFactory;
import org.flexdock.docking.DockingStrategy;
import org.flexdock.docking.drag.effects.DragPreview;
import org.flexdock.perspective.PerspectiveFactory;

import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class DockingModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		install(new ResourceModule());
		
		// Provide default resource values for docking-related stuff (mainly icons)
		Resources.bindPackageBundles(binder(), DockingModule.class);

		bind(DockableFactory.class)
			.to(GutsDockableFactory.class).in(Scopes.SINGLETON);
		bind(PerspectiveFactory.class)
			.to(DefaultPerspectiveFactory.class).in(Scopes.SINGLETON);
		bind(DragPreview.class)
			.to(CursorTrackGhostPreview.class).in(Scopes.SINGLETON);

		// Defaults for ViewportFactory along with DockingStrategy
		bind(EmptyableViewportDockingStrategy.class).in(Scopes.SINGLETON);
		bind(ViewportFactory.class).to(EmptyableViewportDockingStrategy.class);
		bind(DockingStrategy.class).to(EmptyableViewportDockingStrategy.class);

		// Make sure Map<Class<?>, DockingStrategy> is injectable
		Docking.strategies(binder());

		// Make sure Map<String, PerspectiveInitializer> is injectable
		Docking.perspectives(binder());
	}

	@Override public boolean equals(Object other)
	{
		return other instanceof DockingModule;
	}

	@Override public int hashCode()
	{
		return DockingModule.class.hashCode();
	}
}
