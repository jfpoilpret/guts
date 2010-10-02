package net.guts.gui.simple.provider.application;

import java.awt.Window;

import javax.swing.RootPaneContainer;

import com.google.inject.ImplementedBy;

@ImplementedBy(AP_WindowControllerImpl.class)
public interface AP_WindowController {

	Window getActiveWindow();

	void show(RootPaneContainer container);

	void show(RootPaneContainer container, BoundsPolicy bounds,
			StatePolicy state);

}
