package net.guts.gui.docking;

import javax.swing.JComponent;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
class DefaultViewContentFactory implements ViewContentFactory
{
	@Inject DefaultViewContentFactory(Injector injector, DockingSetup setup)
	{
		_injector = injector;
		_setup = setup;
	}
	
	@Override public JComponent create(String id)
	{
		Class<? extends JComponent> clazz = _setup.getDockableComponent(id);
		return (clazz != null ? _injector.getInstance(clazz) : null);
	}

	final private Injector _injector;
	final private DockingSetup _setup;
}
