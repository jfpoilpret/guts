package net.guts.gui.docking;

import javax.swing.JComponent;

import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.intern.DefaultCDockable;

public class DefaultDockableFactory implements DockableFactory
{
	@Override public MultipleCDockable createMulti(String idFactory, 
		MultipleCDockableFactory<?, ?> factory, String id, JComponent content)
	{
		return init(new DefaultMultipleCDockable(factory, content));
	}

	@Override public SingleCDockable createSingle(String id, JComponent content)
	{
		return init(new DefaultSingleCDockable(id, content));
	}
	
	protected <T extends DefaultCDockable> T init(T dockable)
	{
		dockable.setCloseable(true);
		return dockable;
	}
}
