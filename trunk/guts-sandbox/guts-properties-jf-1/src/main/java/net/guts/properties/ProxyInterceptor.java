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

import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

// What about support for beans that already have PCL (eg Swing components)?
class ProxyInterceptor<T> implements MethodInterceptor
{
	ProxyInterceptor(T source, PropertyDescriptor[] properties)
	{
		_source = source;
		_properties = properties;
		_support = new PropertyChangeSupport(_source);
	}

	//CSOFF: IllegalThrows
	@Override public Object intercept(
		Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable
	{
		// Is it one of ChangeListenerAdapter methods?
		Method pclMethod = _pclMethods.get(proxy.getSignature());
		if (pclMethod != null)
		{
			return pclMethod.invoke(_support, args);
		}

		// Is it ProxySource method
		if (proxy.getSignature().equals(_proxySourceMethod))
		{
			return _source;
		}

		// Is it a property setter?
		for (PropertyDescriptor descriptor : _properties)
		{
			if (method.equals(descriptor.getWriteMethod()))
			{
				// If there's a getter, read _source old value, else consider it
				// null
				Object oldValue = null;
				if (descriptor.getReadMethod() != null)
				{
					oldValue = descriptor.getReadMethod().invoke(_source);
				}

				// Call _source setter
				proxy.invoke(_source, args);

				// Call PCL with old & new value
				Object newValue = args[0];
				_support.firePropertyChange(descriptor.getName(), oldValue, newValue);

				return null;
			}
		}

		// Else just call _source methods
		return proxy.invoke(_source, args);
	}
	//CSON: IllegalThrows

	// Holds all useful PropertyChangeSupport listener methods so that we
	// can directly map to these during interception
	private static final Map<Signature, Method> _pclMethods = new HashMap<Signature, Method>();

	//CSOFF: IllegalCatch
	static
	{
		Method[] pclMethods = ChangeListenerAdapter.class.getDeclaredMethods();
		try
		{
			for (Method source : pclMethods)
			{
				Method target = PropertyChangeSupport.class.getMethod(
						source.getName(), source.getParameterTypes());
				_pclMethods.put(ReflectUtils.getSignature(source), target);
			}
		}
		catch (Exception e)
		{
			// Should never happen normally!
			e.printStackTrace();
		}
	}
	//CSON: IllegalCatch

	private static Signature _proxySourceMethod;

	//CSOFF: IllegalCatch
	static
	{
		try
		{
			_proxySourceMethod = 
				ReflectUtils.getSignature(ProxySource.class.getMethod("source"));
		}
		catch (Exception e)
		{
			// Should never happen normally!
			e.printStackTrace();
		}
	}
	//CSON: IllegalCatch

	private final T _source;
	private final PropertyDescriptor[] _properties;
	private final PropertyChangeSupport _support;
}
