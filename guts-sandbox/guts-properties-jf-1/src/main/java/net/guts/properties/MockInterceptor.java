package net.guts.properties;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

class MockInterceptor implements MethodInterceptor
{
	MockInterceptor(PropertyDescriptor[] properties)
	{
		_properties = properties;
	}

	PropertyDescriptor lastUsedProperty()
	{
		PropertyDescriptor property = _lastUsedProperty.get();
		_lastUsedProperty.set(null);
		return property;
	}

	@Override public Object intercept(
		Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable
	{
		_lastUsedProperty.set(null);

		// Check this is a getter
		for (PropertyDescriptor descriptor : _properties)
		{
			if (method.equals(descriptor.getReadMethod()))
			{
				_lastUsedProperty.set(descriptor);
				break;
			}
		}

		// TODO try to return something non-null when possible
		// TODO should we call super method if not abstract of course)?

		return null;
	}

	private final PropertyDescriptor[] _properties;
	//TODO push descriptors to a stack for chained calls of getters in different mocks?
	private final ThreadLocal<PropertyDescriptor> _lastUsedProperty = 
		new ThreadLocal<PropertyDescriptor>();
}
