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

package net.guts.common.bean;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
class UntypedPropertyFactoryImpl implements UntypedPropertyFactory
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(UntypedPropertyFactoryImpl.class);
	
	@Override public UntypedProperty property(String name, Class<?> bean)
	{
		// Check that "bean" has "name" property
		String key = key(name, bean);
		UntypedProperty property = _properties.get(key(name, bean));
		if (property == null && !_properties.containsKey(key))
		{
			try
			{
				property = new UntypedProperty(new PropertyDescriptor(name, bean));
			}
			catch (IntrospectionException e)
			{
				// Too bad. We just record the fact in the _descriptors cache (as null value)
			}
			_properties.put(key, property);
		}
		if (property == null)
		{
			_logger.info("Invalid property `{}` in class `{}`", name, bean);
		}
		return property;
	}

	@Override public UntypedProperty property(String name, Class<?> bean, Class<?> type)
	{
		// Find matching property
		UntypedProperty property = property(name, bean);
		if (property == null)
		{
			return null;
		}
		// Check that property type matches required type
		Class<?> actualType = property.type();
		if (type != actualType)
		{
			_logger.info(
				"Invalid type for property `{}` in class `{}`; expected = `{}`, actual = `{}`",
				new Object[]{name, bean, type, actualType});
			return null;
		}
		return property;
	}

/*
	@SuppressWarnings("unchecked") @Override 
	public <T, V> Property<T, V> property(String name, Class<T> bean, TypeLiteral<V> type)
	{
		// Find matching property
		Property<T, ?> property = property(name, bean);
		if (property == null)
		{
			return null;
		}
		// Check that property type matches required type
		//TODO real type check!!!!
//		TypeLiteral<?> actualType;
//		if (type != actualType)
//		{
//			_logger.info(
//				"Invalid type for property `{}` in class `{}`; expected = `{}`, actual = `{}`",
//				new Object[]{name, bean, type, actualType});
//			return null;
//		}
		return (Property<T, V>) property;
	}
*/
	
	static private String key(String name, Class<?> target)
	{
		return target.getName() + ":" + name;
	}

	final private Map<String, UntypedProperty> _properties = 
		new HashMap<String, UntypedProperty>();
}
