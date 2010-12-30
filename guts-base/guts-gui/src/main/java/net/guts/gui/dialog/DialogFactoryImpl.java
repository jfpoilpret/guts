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

package net.guts.gui.dialog;

import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.guts.gui.window.ActiveWindow;
import net.guts.gui.window.BoundsPolicy;
import net.guts.gui.window.RootPaneConfig;
import net.guts.gui.window.StatePolicy;
import net.guts.gui.window.WindowController;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
class DialogFactoryImpl implements DialogFactory
{
	@Inject DialogFactoryImpl(Injector injector, WindowController windowController, 
		ActiveWindow activeWindow)
	{
		_injector = injector;
		_windowController = windowController;
		_activeWindow = activeWindow;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.dialog.DialogFactory#showDialog(javax.swing.JComponent, net.guts.gui.application.WindowController.BoundsPolicy, boolean)
	 */
	@Override public boolean showDialog(
		JComponent panel, BoundsPolicy bounds, StatePolicy state)
	{
		resetPanel(panel);
		return show(panel, bounds, state);
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.dialog.DialogFactory#showDialog(java.lang.Class, net.guts.gui.application.WindowController.BoundsPolicy, boolean)
	 */
	@Override public <T extends JComponent> boolean showDialog(
		Class<T> clazz, BoundsPolicy bounds, StatePolicy state)
	{
		return showDialog(clazz, bounds, state, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.dialog.DialogFactory#showDialog(java.lang.Class, net.guts.gui.dialog.ComponentInitializer, net.guts.gui.application.WindowController.BoundsPolicy)
	 */
	@Override public <T extends JComponent> boolean showDialog(Class<T> clazz, 
		BoundsPolicy bounds, StatePolicy state, PanelInitializer<T> initializer)
	{
		T panel = _injector.getInstance(clazz);
		resetPanel(panel);
		if (initializer != null)
		{
			initializer.init(panel);
		}
		return show(panel, bounds, state);
	}

	static private void resetPanel(JComponent panel)
	{
		if (panel instanceof Resettable)
		{
			((Resettable) panel).reset();
		}
	}
	
	private boolean show(JComponent panel, BoundsPolicy bounds, StatePolicy state)
	{
		// Find right parent first
		Window active = _activeWindow.get();
		GDialog dialog;
		if (active instanceof JDialog)
		{
			dialog = new GDialog((JDialog) active, panel);
		}
		else if (active instanceof JFrame)
		{
			dialog = new GDialog((JFrame) active, panel);
		}
		else
		{
			// No currently active parent
			dialog = new GDialog((JFrame) null, panel);
		}
		dialog.init();
		_windowController.show(
			dialog, RootPaneConfig.forDialog().bounds(bounds).state(state).config());
		return !dialog.wasCancelled();
	}

	final private Injector _injector;
	final private WindowController _windowController;
	final private ActiveWindow _activeWindow;
}
