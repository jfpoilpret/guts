package net.guts.gui.docking;

import net.guts.event.EventModule;
import net.guts.event.Events;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.session.SessionModule;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class DockingModule extends AbstractModule
{
	@Override protected void configure()
	{
		install(new EventModule());
		install(new ResourceModule());
		install(new SessionModule());

		// Bindings for DockingFrames components
		bind(CControl.class).in(Scopes.SINGLETON);
		bind(ViewFactory.class).to(DefaultViewFactory.class);
		bind(DefaultDockableFactory.class).in(Scopes.SINGLETON);
		// The object below must be constructed eagerly because it is never injected anywhere
		bind(DockingInitializer.class).asEagerSingleton();
		
		// Events bindings
		Events.bindChannel(binder(), CDockable.class);
		
		// Make sure Map<String, DockingLayoutInitializer> is injectable
		Docking.layouts(binder());

		// Make sure Map<String, Class<? extends JComponent> (views) is injectable
		Docking.views(binder());
		
		// Make sure special areas IDs are injectable
		Docking.contentAreas(binder());
		Docking.workingAreas(binder());
		Docking.gridAreas(binder());
		Docking.minimizeAreas(binder());
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
