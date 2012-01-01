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
import javax.swing.RootPaneContainer;

import net.guts.gui.action.GutsActionAdapter;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.GutsAction.ObserverPosition;
import net.guts.gui.action.GutsActionWrapper;
import net.guts.gui.template.TemplateHelper;
import net.guts.gui.template.wizard.Wizard.Result;

// This exists only to ensure correct resource injection
// This allows WizardModule to bind it to guts-gui resource bundle
final class WizardActions
{
	WizardActions(RootPaneContainer container, WizardConfig config)
	{
		_ok = createClosingAction(
			container, "finish", config._apply, config, Result.FINISH);
		_cancel = createClosingAction(
			container, "cancel", config._cancel, config, Result.CANCEL);
	}
	
	static private GutsAction createClosingAction(final RootPaneContainer container,
		String name, Action action,
		final WizardConfig config, final Wizard.Result result)
	{
		if (action != null)
		{
			// Important: it is important to create an anonymous class here so that it
			// is recognized by ResourceInjector as belonging to OkCancelActions, and then
			// it is correctly injected
			GutsAction wrapper = new GutsActionWrapper(name, action){};
			wrapper.addActionObserver(ObserverPosition.LAST, new GutsActionAdapter()
			{
				@Override protected void afterActionPerform()
				{
					config._result = result;
					TemplateHelper.close(container);
				}
			});
			return wrapper;
		}
		else
		{
			return new GutsAction(name)
			{
				@Override protected void perform()
				{
					config._result = result;
					TemplateHelper.close(container);
				}
			};
		}
	}
	
	//CSOFF: VisibilityModifier
	final GutsAction _ok;
	final GutsAction _cancel;
	//CSON: VisibilityModifier
}
