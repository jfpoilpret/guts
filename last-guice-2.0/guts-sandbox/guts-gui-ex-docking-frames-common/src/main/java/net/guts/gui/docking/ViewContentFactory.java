package net.guts.gui.docking;

import javax.swing.JComponent;

import com.google.inject.ImplementedBy;

@ImplementedBy(DefaultViewContentFactory.class)
public interface ViewContentFactory
{
	public JComponent create(String id);
}
