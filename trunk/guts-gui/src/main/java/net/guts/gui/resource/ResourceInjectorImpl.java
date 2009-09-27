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

package net.guts.gui.resource;

import java.awt.Component;
import java.awt.Container;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class ResourceInjectorImpl implements ResourceInjector
{
	@Inject
	public ResourceInjectorImpl(
		ResourceBundleRegistry registry, Map<Class<?>, ComponentInjector<?>> injectors)
	{
		_registry = registry;
		_injectors = injectors;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.resource.ResourceInjector#injectComponent(javax.swing.JComponent)
	 */
	@SuppressWarnings("unchecked") @Override 
	public void injectComponent(Component component)
	{
		// Find the most specific ComponentInjector for component
		Class<? extends Component> type = component.getClass();
		ComponentInjector<Component> injector = (ComponentInjector<Component>) 
			TypeHelper.findBestMatchInTypeHierarchy(_injectors, type);
		ResourceMap resources = _registry.getBundle(type);
		injector.inject(component, resources);
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.resource.ResourceInjector#injectComponents(javax.swing.JComponent)
	 */
	@Override public void injectHierarchy(Component component)
	{
		// First inject component itself
		injectComponent(component);
		// Then recursively inject all its children
		if (component instanceof Container)
		{
			for (Component child: ((Container) component).getComponents())
			{
				injectHierarchy(child);
			}
		}
	}

	final private ResourceBundleRegistry _registry;
	final private Map<Class<?>, ComponentInjector<?>> _injectors;
}
