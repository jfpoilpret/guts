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
import org.flexdock.perspective.persist.PersistenceHandler;
import org.flexdock.view.View;

import net.guts.event.EventModule;
import net.guts.event.Events;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;
import net.guts.gui.session.SessionModule;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class DockingModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		install(new EventModule());
		install(new ResourceModule());
		install(new SessionModule());
		
		// Provide default resource values for docking-related stuff (mainly icons)
		Resources.bindPackageBundles(binder(), DockingModule.class);
		
		// Bind Channel<View>, triggered whenever the focused view changes
		Events.bindChannel(binder(), View.class);
		
		// Make sure perspective will be stored/restored with guts-gui SessionManager
		bind(PersistenceHandler.class).to(SessionPersistenceHandler.class);

		bind(DockableFactory.class)
			.to(GutsDockableFactory.class).in(Scopes.SINGLETON);
		bind(PerspectiveFactory.class)
			.to(DefaultPerspectiveFactory.class).in(Scopes.SINGLETON);
		bind(DragPreview.class)
			.to(CursorTrackGhostPreview.class).in(Scopes.SINGLETON);

		// Defaults for ViewportFactory along with DockingStrategy
		bind(GutsViewportDockingStrategy.class).in(Scopes.SINGLETON);
		//TODO remove later on
		bind(ViewportFactory.class).to(GutsViewportDockingStrategy.class);
		bind(DockingStrategy.class).to(GutsViewportDockingStrategy.class);

		// Make sure Map<String, PerspectiveInitializer> is injectable
		Docking.perspectives(binder());

		// Make sure Map<String, Class<? extends JComponent> (views) is injectable
		Docking.views(binder());
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
