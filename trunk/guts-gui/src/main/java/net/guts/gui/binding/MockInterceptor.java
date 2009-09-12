package net.guts.gui.binding;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import com.google.inject.internal.cglib.proxy.MethodInterceptor;
import com.google.inject.internal.cglib.proxy.MethodProxy;

class MockInterceptor implements MethodInterceptor
{
	public MockInterceptor(PropertyDescriptor[] properties)
	{
		_properties = properties;
	}
	
	public PropertyDescriptor lastUsedProperty()
	{
		PropertyDescriptor property = _lastUsedProperty;
		_lastUsedProperty = null;
		return property;
	}
	
	public Object intercept(
		Object target, Method method, Object[] args, MethodProxy proxy)
		throws Throwable
	{
		_lastUsedProperty = null;
		// Check this is a getter
		for (PropertyDescriptor descriptor: _properties)
		{
			if (method.equals(descriptor.getReadMethod()))
			{
				_lastUsedProperty = descriptor;
				break;
			}
		}
		//TODO always return something non-null when possible, so to add 
		// some type-checking in Binding class
		// => should we call super method if not abstract of course)?
		return null;
	}

	private final PropertyDescriptor[] _properties;
	// Note: this is not in a ThreadLocal because this should be used ONLY in EDT!
	private PropertyDescriptor _lastUsedProperty = null;
}
