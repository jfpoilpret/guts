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

import net.guts.gui.application.WindowController;
import net.guts.gui.application.WindowController.BoundsPolicy;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
class DialogFactoryImpl implements DialogFactory
{
	@Inject DialogFactoryImpl(Injector injector, WindowController windowController)
	{
		_injector = injector;
		_windowController = windowController;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.DialogFactory#showDialog(javax.swing.JComponent)
	 */
	public boolean showDialog(JComponent panel)
	{
		return showDialog(panel, BoundsPolicy.PACK_AND_CENTER);
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.dialog.DialogFactory#showDialog(javax.swing.JComponent, net.guts.gui.application.WindowController.BoundsPolicy)
	 */
	public boolean showDialog(JComponent panel, BoundsPolicy policy)
	{
		resetPanel(panel);
		return show(panel, policy);
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.DialogFactory#showDialog(java.lang.Class)
	 */
	public <T extends JComponent> boolean showDialog(Class<T> clazz)
	{
		return showDialog(clazz, null, BoundsPolicy.PACK_AND_CENTER);
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.dialog.DialogFactory#showDialog(java.lang.Class, net.guts.gui.application.WindowController.BoundsPolicy)
	 */
	public <T extends JComponent> boolean showDialog(Class<T> clazz, BoundsPolicy policy)
	{
		return showDialog(clazz, null, policy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.DialogFactory#showDialog(java.lang.Class, net.sf.guice.gui.dialog.ComponentInitializer)
	 */
	public <T extends JComponent> boolean showDialog(
		Class<T> clazz, ComponentInitializer<T> initializer)
	{
		return showDialog(clazz, initializer, BoundsPolicy.PACK_AND_CENTER);
	}

	public <T extends JComponent> boolean showDialog(
		Class<T> clazz, ComponentInitializer<T> initializer, BoundsPolicy policy)
	{
		T panel = _injector.getInstance(clazz);
		resetPanel(panel);
		if (initializer != null)
		{
			initializer.init(panel);
		}
		return show(panel, policy);
	}

	static private void resetPanel(JComponent panel)
	{
		if (panel instanceof Resettable)
		{
			((Resettable) panel).reset();
		}
	}
	
	private boolean show(JComponent panel, BoundsPolicy policy)
	{
		// Find right parent first
		Window active = _windowController.getActiveWindow();
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
		_windowController.show(dialog, BoundsPolicy.PACK_AND_CENTER);
		return !dialog.wasCancelled();
	}

	final private Injector _injector;
	final private WindowController _windowController;
}
