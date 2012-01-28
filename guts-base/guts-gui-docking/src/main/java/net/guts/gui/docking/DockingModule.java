package net.guts.gui.docking;

import net.guts.common.injection.AbstractSingletonModule;
import net.guts.event.EventModule;
import net.guts.event.Events;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.session.SessionModule;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;

import com.google.inject.Scopes;

/**
 * Guice {@link com.google.inject.Module} for Docking Frames integration into
 * Guts-GUI framework. This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new DockingModule(), ...);
 * </pre>
 * <p/>
 * Or, in a typical Guts-GUI application:
 * <pre>
 * public class MyApplication extends AbstractApplication
 * {
 *     ...
 *     &#63;Override protected void initModules(String[] args, List&lt;Module&gt; modules)
 *     {
 *         modules.add(new DockingModule());
 *         modules.add(new MyApplicationModule());
 *     }
 * }
 * </pre>
 * {@code DockingModule} makes the following bindings available to your application:
 * <ul>
 * TODO list of bindings available
 * </ul>
 * TODO must define binding for DockingLifecycle subclass
 *
 * @author Jean-Francois Poilpret
 */
public final class DockingModule extends AbstractSingletonModule
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
}
