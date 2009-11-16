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

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

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
			property = findProperty(bean, name);
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
		Class<?> actualType = property.type().getRawType();
		if (type != actualType)
		{
			_logger.info(
				"Invalid type for property `{}` in class `{}`; expected = `{}`, actual = `{}`",
				new Object[]{name, bean, type, actualType});
			return null;
		}
		return property;
	}
	
	@Override public UntypedProperty property(String name, Class<?> bean, TypeLiteral<?> type)
	{
		// Find matching property
		UntypedProperty property = property(name, bean);
		if (property == null)
		{
			return null;
		}
		// Check that property type matches required type
		TypeLiteral<?> actualType = property.type();
		if (!type.equals(actualType))
		{
			_logger.info(
				"Invalid type for property `{}` in class `{}`; expected = `{}`, actual = `{}`",
				new Object[]{name, bean, type, actualType});
			return null;
		}
		return property;
	}
	
	static private UntypedProperty findProperty(Class<?> bean, String name)
	{
		// First find if there is a getter (is or get property)
		String propertyName = capitalize(name);
		Method getter = findMethod(bean, "is" + propertyName, null);
		Method setter = null;
		if (getter == null)
		{
			getter = findMethod(bean, "get" + propertyName, null);
		}
		if (getter != null)
		{
			// Then find a setter for that property, if exists
			Type type = getter.getGenericReturnType();
			setter = findMethod(bean, "set" + propertyName, TypeLiteral.get(type));
			// Return a new property
			return new UntypedProperty(name, TypeLiteral.get(type), setter, getter);
		}

		// Else try to find a setter (in the list of all methods)
		setter = findAnySetter(bean, "set" + propertyName);
		if (setter != null)
		{
			return new UntypedProperty(
				name, TypeLiteral.get(setter.getGenericParameterTypes()[0]), setter, null);
		}
		
		return null;
	}
	
	static private Method findMethod(Class<?> bean, String name, TypeLiteral<?> type)
	{
		while (bean != null)
		{
			try
			{
				if (type == null)
				{
					return bean.getDeclaredMethod(name);
				}
				Method method =  bean.getDeclaredMethod(name, type.getRawType());
				if (method.getGenericParameterTypes()[0].equals(type.getType()))
				{
					return method;
				}
				else
				{
					return null;
				}
			}
			catch (Exception e)
			{
				bean = bean.getSuperclass();
			}
		}
		return null;
	}
	
	static private Method findAnySetter(Class<?> bean, String name)
	{
		while (bean != null)
		{
			try
			{
				for (Method method: bean.getDeclaredMethods())
				{
					if (	method.getName().equals(name)
						&&	method.getParameterTypes().length == 1)
					{
						return method;
					}
				}
			}
			catch (Exception e)
			{
				// Fall back to superclass check
			}
			bean = bean.getSuperclass();
		}
		return null;
	}
	
	static private String capitalize(String name)
	{
		if (name == null || name.length() == 0)
		{
			return name; 
		}
		return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
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
