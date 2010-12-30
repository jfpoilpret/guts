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

package net.guts.gui.dialogalt;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import net.guts.gui.window.ActiveWindow;
import net.guts.gui.window.BoundsPolicy;
import net.guts.gui.window.JDialogConfig;
import net.guts.gui.window.StatePolicy;
import net.guts.gui.window.WindowController;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton class DialogFactoryImpl2 implements DialogFactory2
{
	@Inject DialogFactoryImpl2(Injector injector, WindowController windowController,
		ActiveWindow activeWindow)
	{
		_injector = injector;
		_windowController = windowController;
		_activeWindow = activeWindow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.guts.gui.dialog.DialogFactory#showDialog(javax.swing.JComponent,
	 * net.guts.gui.application.WindowController.BoundsPolicy, boolean)
	 */
	public boolean showDialog(JComponent panel, BoundsPolicy bounds, StatePolicy state)
	{
		return show(panel, bounds, state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.guts.gui.dialog.DialogFactory#showDialog(java.lang.Class,
	 * net.guts.gui.dialog.ComponentInitializer,
	 * net.guts.gui.application.WindowController.BoundsPolicy)
	 */
	public <T extends JComponent> boolean showDialog(Class<T> clazz, BoundsPolicy bounds,
		StatePolicy state)
	{
		T panel = _injector.getInstance(clazz);
		return show(panel, bounds, state);
	}

	private boolean show(JComponent panel, BoundsPolicy bounds, StatePolicy state)
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
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		SimpleDialogViewCloser viewCloser = new SimpleDialogViewCloser(dialog);
		dialog.addWindowListener(new CloseListener(viewCloser));

		DialogTemplate template = _injector.getInstance(DialogTemplate.class);
		template.setView(panel);
		template.setViewCloser(viewCloser);

		dialog.setContentPane(template);

		_windowController.show(dialog, 
			JDialogConfig.create().bounds(bounds).state(state).config());

		return !template.wasCancelled();
	}

	static private class CloseListener extends WindowAdapter
	{
		private final ViewCloser _viewCloser;

		public CloseListener(ViewCloser viewCloser)
		{
			_viewCloser = viewCloser;
		}

		@Override public void windowClosing(WindowEvent event)
		{
			_viewCloser.doClose();
		}
	}

	private static class SimpleDialogViewCloser implements ViewCloser
	{
		private final JDialog _dialog;

		public SimpleDialogViewCloser(JDialog dialog)
		{
			_dialog = dialog;
		}

		public boolean doClose()
		{
			_dialog.setVisible(false);
			_dialog.dispose();
			return true;
		}
	}

	final private Injector _injector;
	final private WindowController _windowController;
	final private ActiveWindow _activeWindow;
}
