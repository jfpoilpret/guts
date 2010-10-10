package net.guts.gui.docking;

import java.util.Set;

import javax.swing.JComponent;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultDockingSetup.class)
public interface DockingSetup
{
	public Set<String> listDockables();
	public Class<? extends JComponent> getDockableComponent(String id);
	public Set<String> listContentAreas();
	public Set<String> listWorkingAreas();
	public Set<String> listGridAreas();
	public Set<String> listMinimizeAreas();
}
