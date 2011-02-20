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
import net.guts.gui.window.RootPaneConfig;
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
	
	@Override public void showDialog(JComponent view, RootPaneConfig<JDialog> config)
	{
		// Create dialog
		JDialog dialog = createDialog();
		dialog.setName(view.getName() + "-dialog");
		dialog.setContentPane(view);
		// Show the dialog
		_windowController.show(dialog, config);
	}

	@Override public void showDialog(
		Class<? extends JComponent> viewClass, RootPaneConfig<JDialog> config)
	{
		JComponent view = _injector.getInstance(viewClass);
		showDialog(view, config);
	}

	private JDialog createDialog()
	{
		// Find right parent first
		Window active = _activeWindow.get();
		JDialog dialog;
		if (active instanceof JDialog)
		{
			dialog = new JDialog((JDialog) active);
		}
		else if (active instanceof JFrame)
		{
			dialog = new JDialog((JFrame) active);
		}
		else
		{
			// No currently active parent
			dialog = new JDialog((JFrame) null);
		}
		dialog.setModal(true);
		return dialog;
	}

	final private Injector _injector;
	final private ActiveWindow _activeWindow;
	final private WindowController _windowController;
}
