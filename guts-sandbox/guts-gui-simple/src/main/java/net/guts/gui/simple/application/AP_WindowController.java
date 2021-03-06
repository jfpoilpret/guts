package net.guts.gui.simple.application;

import java.awt.Window;

import javax.swing.RootPaneContainer;

import net.guts.gui.window.BoundsPolicy;
import net.guts.gui.window.StatePolicy;

import com.google.inject.ImplementedBy;

@ImplementedBy(AP_WindowControllerImpl.class)
public interface AP_WindowController {

	Window getActiveWindow();

	void show(RootPaneContainer container);

	void show(RootPaneContainer container, BoundsPolicy bounds,
			StatePolicy state);

}
