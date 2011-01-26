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

package net.guts.gui.dialog2.template;

import javax.swing.RootPaneContainer;

import net.guts.gui.window.RootPaneConfig;
import net.guts.gui.window.WindowProcessor;

import com.google.inject.Inject;
import com.google.inject.Injector;

class WpTemplateDecoration implements WindowProcessor
{
	@Inject WpTemplateDecoration(Injector injector)
	{
		_injector = injector;
	}
	
	@Override 
	public <T extends RootPaneContainer> void process(T root, RootPaneConfig<T> config)
	{
		// Decorate the container
		TemplateDecorator template = getTemplate(config);
		if (template != null)
		{
			template.decorate(root, root.getContentPane(), config);
		}
	}
	
	private TemplateDecorator getTemplate(RootPaneConfig<?> config)
	{
		Class<? extends TemplateDecorator> clazz = 
			config.get(TemplateDecorator.TEMPLATE_TYPE_KEY);
		if (clazz != null)
		{
			return _injector.getInstance(clazz);
		}
		else
		{
			return null;
		}
	}
	
	final private Injector _injector;
}
