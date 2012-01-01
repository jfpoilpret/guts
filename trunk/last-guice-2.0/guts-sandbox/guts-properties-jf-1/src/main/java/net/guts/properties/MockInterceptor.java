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

package net.guts.properties;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

class MockInterceptor implements MethodInterceptor
{
	MockInterceptor(PropertyDescriptor[] properties)
	{
		_properties = properties;
	}

	//CSOFF: IllegalThrows
	@Override public Object intercept(
		Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable
	{
		// Check this is a getter
		for (PropertyDescriptor descriptor : _properties)
		{
			if (method.equals(descriptor.getReadMethod()))
			{
				RecordedGetters.pushProperty(descriptor);

				//  Return a new Bean mock when possible
				return createDefault(descriptor.getPropertyType());
			}
		}

		// For any non-property call, we just don't allow it
		String msg = String.format(
			"Calling `%s.%s` is not supported. Only getters can be called", 
			method.getDeclaringClass().getSimpleName(), method.getName());
		throw new UnsupportedOperationException(msg);
	}
	//CSON: IllegalThrows

	//CSOFF: IllegalCatch
	static private Object createDefault(Class<?> type)
	{
		Object defaultValue = DEFAULTS.get(type);
		if (defaultValue != null)
		{
			return defaultValue;
		}
		else if (Modifier.isFinal(type.getModifiers()))
		{
			// Try reflection to instantiate a default instance
			try
			{
				return type.newInstance();
			}
			catch (Exception e)
			{
				return null;
			}
		}
		else
		{
			try
			{
				return Bean.create(type).mock();
			}
			catch (Exception e)
			{
				return null;
			}
		}
	}
	//CSON: IllegalCatch
	
	static private final Map<Class<?>, Object> DEFAULTS = new HashMap<Class<?>, Object>();
	
	static
	{
		DEFAULTS.put(byte.class, (byte) 0);
		DEFAULTS.put(Byte.class, (byte) 0);
		DEFAULTS.put(char.class, (char) 0);
		DEFAULTS.put(Character.class, (char) 0);
		DEFAULTS.put(short.class, (short) 0);
		DEFAULTS.put(Short.class, (short) 0);
		DEFAULTS.put(int.class, 0);
		DEFAULTS.put(Integer.class, 0);
		DEFAULTS.put(long.class, 0L);
		DEFAULTS.put(Long.class, 0L);
		DEFAULTS.put(float.class, 0.0f);
		DEFAULTS.put(Float.class, 0.0f);
		DEFAULTS.put(double.class, 0.0);
		DEFAULTS.put(Double.class, 0.0);
		DEFAULTS.put(boolean.class, false);
		DEFAULTS.put(Boolean.class, false);
		DEFAULTS.put(String.class, "");
	}

	private final PropertyDescriptor[] _properties;
}
