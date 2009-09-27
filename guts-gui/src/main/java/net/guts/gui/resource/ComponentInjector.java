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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.cglib.beans.BeanMap;


//TODO find a better name like SingleResourceInjector? InstanceResourcesInjector?...
//TODO create impl that simply injects through bean properties
//TODO there must be some recursivity here (because components can contain other components)
// Maybe need to pass a map of ComponentInjectors?
/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
public interface ComponentInjector<T>
{
	// resources is the list of all resources (strongly typed) for this
	// component
	// Implementations of this method should iterate through resources and
	// inject each individual resource into component
	public void inject(T component, ResourceMap resources);
}

// Default component injector
class BeanPropertiesInjector implements ComponentInjector<Component>
{
	static final private Logger _logger = 
		LoggerFactory.getLogger(BeanPropertiesInjector.class);
	
	@Override public void inject(Component component, ResourceMap resources)
	{
		String prefix = component.getName();
		Class<?> componentType = component.getClass();
		if (prefix == null)
		{
			String msg = String.format("Component has no name: %d", component);
			_logger.info(msg);
			return;
		}
		// Get the right BeanMap for that component
		BeanMap bean = _beans.get(componentType);
		if (bean == null)
		{
			bean = BeanMap.create(component);
			bean.setBean(null);
			_beans.put(componentType, bean);
		}
//		else
//		{
//			bean.setBean(component);
//		}
		// For each injectable resource
		for (String key: resources.keys(prefix))
		{
			//TODO Use cglib ReflectUtils.getBeanSetters? 
			// -> Does it cover all class hierarchy?
			// -> Should the result be cached in a Map<Class, PropertyDescriptor>
			// Can we use BulkBean?
			// Or better to use BeanMap => seems not bad!

			// Check that this property exists
			Class<?> type = bean.getPropertyType(key);
			if (type == null)
			{
				String msg = String.format("Unknow property `%s` of type `%s`", 
					key, componentType);
				_logger.info(msg);
				continue;
			}
			// Get the value in the correct type
			Object value = resources.getValue(prefix, key, type);
			// Set the property with the resource value
			bean.put(component, key, value);
		}
	}

	//FIXME probably not a terrific idea:
	// - requires cglib in classpath (in addition to "guice cglib"
	// - holds a reference to a component forever!
	// Good enough for a first prototype however!
	final private Map<Class<?>, BeanMap> _beans = new HashMap<Class<?>, BeanMap>();
}
