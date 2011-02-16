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

package net.guts.gui.template.wizard;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.RootPaneContainer;

import net.guts.gui.template.TemplateDecorator;
import net.guts.gui.window.AbstractConfig;

import com.google.inject.Inject;
import com.google.inject.Injector;

public final class Wizard extends AbstractConfig<RootPaneContainer, Wizard>
{
	private Wizard()
	{
		set(TemplateDecorator.TEMPLATE_TYPE_KEY, WizardDecorator.class);
		set(WizardConfig.class, _config);
		_config._controller = _controller;
	}
	
	static public Wizard create()
	{
		return new Wizard();
	}
	
	public Wizard mapNextStep(JComponent view)
	{
		_controller.addStep(view.getName(), view, true);
		return this;
	}
	
	public Wizard mapOneStep(JComponent view)
	{
		_controller.addStep(view.getName(), view, false);
		return this;
	}
	
	public Wizard mapNextStep(Class<? extends JComponent> view)
	{
		return mapNextStep(_injector.getInstance(view));
	}
	
	public Wizard mapOneStep(Class<? extends JComponent> view)
	{
		return mapOneStep(_injector.getInstance(view));
	}
	
	public Wizard withFinish(Action apply)
	{
		_config._apply = apply;
		return this;
	}
	
	public Wizard withCancel(Action cancel)
	{
		_config._cancel =  cancel;
		return this;
	}

	public Wizard withCancel()
	{
		return withCancel(null);
	}

	public JComponent mainView()
	{
		return _mainView;
	}
	
	public WizardController controller()
	{
		return _controller;
	}
	
	static public enum Result
	{
		OK,
		CANCEL
	}

	public Result result()
	{
		return _config._result;
	}

	@Inject static void setInjector(Injector injector)
	{
		_injector = injector;
	}
	
	static private Injector _injector;
	
	final private WizardConfig _config = new WizardConfig();
	final private JComponent _mainView = new JPanel();
	final private WizardController _controller = new WizardController(_mainView);
}
