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

package net.guts.gui.dialog2;

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
		// Decorate it
		TemplateDecorator template = getTemplate(config);
		if (template != null)
		{
			template.decorate(dialog, view, config);
		}
		else
		{
			dialog.setContentPane(view);
		}
		// Show the dialog
		_windowController.show(dialog, config);
	}

	//TODO no need real generics here just Class<? extends JComponent>
	@Override public <T extends JComponent> void showDialog(
		Class<T> viewClass, RootPaneConfig<JDialog> config)
	{
		T view = _injector.getInstance(viewClass);
		showDialog(view, config);
	}

	private TemplateDecorator getTemplate(RootPaneConfig<JDialog> config)
	{
		Class<? extends TemplateDecorator> clazz = config.get(
			TemplateDecorator.TEMPLATE_TYPE_KEY).asSubclass(TemplateDecorator.class);
		return _injector.getInstance(clazz);
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
//	final private Map<Class<?>, DialogTemplate<?, ?>> _templates;
}
