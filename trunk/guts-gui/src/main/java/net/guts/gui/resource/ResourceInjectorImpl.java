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

import java.util.Map;

import javax.swing.JComponent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

@Singleton
class ResourceInjectorImpl implements ResourceInjector
{
	@Inject
	public ResourceInjectorImpl(Map<Class<?>, ComponentInjector<?>> injectors,
		Map<TypeLiteral<?>, ResourceConverter<?>> converters)
	{
		_injectors = injectors;
		_converters = converters;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.resource.ResourceInjector#injectComponent(javax.swing.JComponent)
	 */
	@Override public void injectComponent(JComponent component)
	{
		// TODO Auto-generated method stub
		//TODO Find the most specific ComponentInjector for component
		// This code already exists somewhere else, try to refactor!
		Class<?> type = component.getClass();
		ComponentInjector<?> injector = null;
		while (true)
		{
			injector = _injectors.get(type);
			if (injector != null)
			{
				break;
			}
			type = type.getSuperclass();
			//TODO Should we handle interfaces as well?
		}
		//TODO get hold of a ResourceMap!
//		injector.inject(component, resources);
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.resource.ResourceInjector#injectComponents(javax.swing.JComponent)
	 */
	@Override public void injectHierarchy(JComponent component)
	{
		// TODO Auto-generated method stub

	}

	final private Map<Class<?>, ComponentInjector<?>> _injectors;
	final private Map<TypeLiteral<?>, ResourceConverter<?>> _converters;
}
