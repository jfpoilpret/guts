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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.type.TypeHelper;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class ResourceInjectorImpl implements ResourceInjector
{
	static final private Logger _logger = LoggerFactory.getLogger(ResourceInjectorImpl.class);

	@Inject	public ResourceInjectorImpl(
		ResourceBundleRegistry registry, Map<Class<?>, InstanceInjector<?>> injectors)
	{
		_registry = registry;
		_injectors = injectors;
	}

	@Override public void injectComponent(Component component)
	{
		if (component != null)
		{
			String prefix = prefix(component);
			if (prefix != null)
			{
				// Find the most specific ComponentInjector for component
				Class<? extends Component> type = component.getClass();
				InstanceInjector<Component> injector = findComponentInjector(type);
				ResourceMap resources = _registry.getBundle(type);
				injector.inject(component, prefix, resources);
			}
		}
	}

	@Override public void injectHierarchy(Component component)
	{
		if (component != null)
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
	}

	@Override public <T> void injectInstance(T instance)
	{
		if (instance != null)
		{
			injectInstance(instance, instance.getClass().getSimpleName());
		}
	}
	
	@Override public <T> void injectInstance(T instance, String name)
	{
		if (instance != null)
		{
			// Find the most specific ComponentInjector for component
			Class<? extends T> type = getClass(instance);
			InstanceInjector<T> injector = findComponentInjector(type);
			ResourceMap resources = _registry.getBundle(type);
			injector.inject(instance, name, resources);
		}
	}

	@SuppressWarnings("unchecked") 
	private <T> Class<? extends T> getClass(T instance)
	{
		return (Class<? extends T>) instance.getClass();
	}
	
	@SuppressWarnings("unchecked") 
	private <T> InstanceInjector<T> findComponentInjector(Class<? extends T> type)
	{
		InstanceInjector<?> injector = 
			TypeHelper.findBestMatchInTypeHierarchy(_injectors, type);
		return (InstanceInjector<T>) injector;
	}
	
	private String prefix(Component component)
	{
		String prefix = component.getName();
		if (prefix == null)
		{
			_logger.debug("Component has no name: {}", component);
		}
		return prefix;
	}

	final private ResourceBundleRegistry _registry;
	final private Map<Class<?>, InstanceInjector<?>> _injectors;
}
