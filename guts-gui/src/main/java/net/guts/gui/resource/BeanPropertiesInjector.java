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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.bean.UntypedProperty;
import net.guts.common.bean.UntypedPropertyFactory;
import net.guts.gui.resource.ResourceMap.Key;

import static net.guts.common.type.PrimitiveHelper.toWrapper;

import com.google.inject.Inject;

public class BeanPropertiesInjector<T> implements ComponentInjector<T>
{
	@Inject final void setPropertyFactory(UntypedPropertyFactory properties)
	{
		_properties = properties;
	}

	@Override public final void inject(T component, String prefix, ResourceMap resources)
	{
		// For each injectable resource
		for (Key key: resources.keys(prefix))
		{
			if (!handleSpecialProperty(component, key, resources))
			{
				injectProperty(component, key, resources);
			}
		}
	}
	
	protected boolean handleSpecialProperty(
		T component, ResourceMap.Key key, ResourceMap resources)
	{
		return false;
	}

	protected void injectProperty(T component, Key key, ResourceMap resources)
	{
		// Check that this property exists
		UntypedProperty property = writableProperty(key.key(), component.getClass());
		if (property != null)
		{
			Class<?> type = property.type();
			// Get the value in the correct type
			Object value = resources.getValue(key, type);
			// Set the property with the resource value
			injectProperty(component, property, value);
		}
	}

	protected void injectProperty(T component, UntypedProperty property, Object value)
	{
		property.set(component, value);
	}
	
	final protected UntypedPropertyFactory properties()
	{
		return _properties;
	}

	final protected UntypedProperty writableProperty(String name, Class<?> bean)
	{
		UntypedProperty property = _properties.property(name, bean);
		if (property == null)
		{
			_logger.debug("Property {} in class {} doesn't exist.", name, bean);
			return null;
		}
		if (!property.isWritable())
		{
			_logger.debug("Property {} in class {} isn't writable.", name, bean);
			return null;
		}
		return property;
	}
	
	final protected void setProperty(T bean, String name, Object value)
	{
		Class<?> type = bean.getClass();
		UntypedProperty property = writableProperty(name, type);
		if (property == null)
		{
			return;
		}
		if (value != null && !toWrapper(property.type()).isAssignableFrom(value.getClass()))
		{
			_logger.debug("Value {} is not assignable to property {} in class {}.", 
				new Object[]{value, name, type});
			return;
		}
		property.set(bean, value);
	}
	
	final protected Logger _logger = LoggerFactory.getLogger(getClass());
	private UntypedPropertyFactory _properties;
}
