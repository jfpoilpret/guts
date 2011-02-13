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
import net.guts.gui.dialog2.template.TemplateDecorator;
import net.guts.gui.dialog2.template.TemplateHelper;
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
		OkCancelActions actions = new OkCancelActions(container, config);
		_actionRegistry.registerActions(actions);

		// Check that if container was already injected, it has the exact same buttons
		// otherwise throw an exception immediately
		if (TemplateHelper.checkCompatibleView(
			view, getClass(), actions._ok, actions._apply, actions._cancel))
		{
			return view;
		}

		// We need to modify the view and add the right buttons to it
		JButton okBtn = TemplateHelper.createButton(actions._ok, view);
		JButton applyBtn = TemplateHelper.createButton(actions._apply, view);
		JButton cancelBtn = TemplateHelper.createButton(actions._cancel, view);

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

	final private ActionRegistrationManager _actionRegistry;
	final private Map<Class<? extends LayoutManager>, OkCancelLayoutAdder> _layouts;
}
