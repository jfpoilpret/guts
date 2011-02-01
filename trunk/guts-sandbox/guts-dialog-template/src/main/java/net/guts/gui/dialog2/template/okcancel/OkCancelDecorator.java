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

import java.awt.Container;
import java.awt.LayoutManager;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.RootPaneContainer;

import net.guts.gui.action.ActionRegistrationManager;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.GutsActionDecorator;
import net.guts.gui.dialog2.template.TemplateDecorator;
import net.guts.gui.dialog2.template.util.TemplateHelper;
import net.guts.gui.window.RootPaneConfig;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class OkCancelDecorator implements TemplateDecorator
{
	@Inject OkCancelDecorator(ActionRegistrationManager actionRegistry,
		Map<Class<? extends LayoutManager>, OkCancelLayoutAdder> layouts)
	{
		_actionRegistry = actionRegistry;
		_layouts = layouts;
	}

	@Override public <T extends RootPaneContainer> void decorate(
		T container, Container view, RootPaneConfig<T> configuration)
	{
		// Get config passed by the initial caller
		OkCancelConfig config = configuration.get(OkCancelConfig.class);

		// Create necessary Actions, lay them out
		Container fullView = installActions(container, view, config);
		
		// Add view to container
		container.setContentPane(fullView);
	}
	
	private Container installActions(
		RootPaneContainer container, Container view, OkCancelConfig config)
	{
		// Create the right actions when needed
		GutsAction ok = setupAction(createOkAction(container, config));
		GutsAction apply = setupAction(createApplyAction(config));
		GutsAction cancel = setupAction(createCancelAction(container, config));

		// Check that if container was already injected, it has the exact same buttons
		// otherwise throw an exception immediately
		if (TemplateHelper.checkCompatibleView(view, getClass(), ok, apply, cancel))
		{
			return view;
		}

		// We need to modify the view and add the right buttons to it
		JButton okBtn = TemplateHelper.createButton(ok, view);
		JButton applyBtn = TemplateHelper.createButton(apply, view);
		JButton cancelBtn = TemplateHelper.createButton(cancel, view);

		if (okBtn != null || applyBtn != null || cancelBtn != null)
		{
			// Add them to the view with the right layout-optimized adder
			Class<? extends LayoutManager> layout = view.getLayout().getClass();
			OkCancelLayoutAdder adder = _layouts.get(layout);
			if (config._dontChangeView || adder == null)
			{
				adder = _layouts.get(LayoutManager.class);
			}
			Container fullView = adder.layout(view, okBtn, cancelBtn, applyBtn);
			if (fullView == view)
			{
				TemplateHelper.setViewModified(fullView, getClass());
			}
			return fullView;
		}
		else
		{
			return view;
		}
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
			return new GutsActionDecorator("apply", config._apply);
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
	
	private GutsAction setupAction(GutsAction action)
	{
		if (action != null)
		{
			_actionRegistry.registerAction(action);
		}
		return action;
	}
	
	final private ActionRegistrationManager _actionRegistry;
	final private Map<Class<? extends LayoutManager>, OkCancelLayoutAdder> _layouts;
}
