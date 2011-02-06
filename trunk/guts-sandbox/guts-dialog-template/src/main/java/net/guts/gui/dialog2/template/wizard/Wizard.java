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

package net.guts.gui.dialog2.template.wizard;

import javax.swing.Action;
import javax.swing.RootPaneContainer;

import net.guts.gui.dialog2.template.TemplateDecorator;
import net.guts.gui.window.AbstractConfig;

public final class Wizard extends AbstractConfig<RootPaneContainer, Wizard>
{
	private Wizard(WizardModel model)
	{
		set(TemplateDecorator.TEMPLATE_TYPE_KEY, WizardDecorator.class);
		set(WizardConfig.class, _config);
		_config._model = model;
	}
	
	static public Wizard forModel(WizardModel model)
	{
		return new Wizard(model);
	}
	
	public Wizard withOK(Action apply)
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

	static public enum Result
	{
		OK,
		CANCEL
	}

	public Result result()
	{
		return _config._result;
	}
	
	final private WizardConfig _config = new WizardConfig();
}
