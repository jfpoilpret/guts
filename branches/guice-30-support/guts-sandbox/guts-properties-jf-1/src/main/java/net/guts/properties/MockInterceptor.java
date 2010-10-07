package net.guts.properties;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

class MockInterceptor implements MethodInterceptor {

	public MockInterceptor(PropertyDescriptor[] properties) {
		_properties = properties;
	}

	public PropertyDescriptor lastUsedProperty() {

		PropertyDescriptor property = _lastUsedProperty;
		_lastUsedProperty = null;

		return property;

	}

	@Override
	public Object intercept(Object target, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {

		_lastUsedProperty = null;

		// Check this is a getter
		for (PropertyDescriptor descriptor : _properties) {
			if (method.equals(descriptor.getReadMethod())) {
				_lastUsedProperty = descriptor;
				break;
			}
		}

		// TODO try to return something non-null when possible
		// TODO should we call super method if not abstract of course)?

		return null;

	}

	private final PropertyDescriptor[] _properties;

	// FIXME should be in a ThreadLocal no?
	private PropertyDescriptor _lastUsedProperty = null;

}
