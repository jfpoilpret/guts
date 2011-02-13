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

package net.guts.gui.dialog2.template.okcancel;

import javax.swing.RootPaneContainer;

import net.guts.gui.action.GutsAction;
import net.guts.gui.action.GutsActionDecorator;
import net.guts.gui.dialog2.template.TemplateHelper;

// This exists only to ensure correct resource injection
// This allows OkCancelModule to bind it to guts-gui resource bundle
final class OkCancelActions
{
	OkCancelActions(RootPaneContainer container, OkCancelConfig config)
	{
		_ok = createOkAction(container, config);
		_apply = createApplyAction(config);
		_cancel = createCancelAction(container, config);
	}
	
	static private GutsAction createOkAction(
		final RootPaneContainer container, final OkCancelConfig config)
	{
		if (config._hasOK && config._apply != null)
		{
			return new GutsActionDecorator("ok", config._apply)
			{
				@Override protected void afterTargetPerform()
				{
					config._result = OkCancel.Result.OK;
					TemplateHelper.close(container);
				}
			};
		}
		else
		{
			return null;
		}
	}
	
	static private GutsAction createApplyAction(OkCancelConfig config)
	{
		if (config._hasApply && config._apply != null)
		{
			// Important: it is important to create an anonymous class here so that it
			// is recognized by ResourceInjector as belonging to OkCancelActions, and then
			// it is correctly injected
			return new GutsActionDecorator("apply", config._apply){};
		}
		else
		{
			return null;
		}
	}
	
	static private GutsAction createCancelAction(
		final RootPaneContainer container, final OkCancelConfig config)
	{
		GutsAction cancel = null;
		if (config._hasCancel)
		{
			if (config._cancel != null)
			{
				cancel = new GutsActionDecorator("cancel", config._cancel)
				{
					@Override protected void afterTargetPerform()
					{
						config._result = OkCancel.Result.CANCEL;
						TemplateHelper.close(container);
					}
				};
			}
			else
			{
				cancel = new GutsAction("cancel")
				{
					@Override protected void perform()
					{
						config._result = OkCancel.Result.CANCEL;
						TemplateHelper.close(container);
					}
				};
			}
		}
		return cancel;
	}

	//CSOFF: VisibilityModifier
	final GutsAction _ok;
	final GutsAction _apply;
	final GutsAction _cancel;
	//CSON: VisibilityModifier
}
