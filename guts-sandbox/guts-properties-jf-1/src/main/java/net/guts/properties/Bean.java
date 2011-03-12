package net.guts.properties;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.proxy.Enhancer;

//TODO add PCL at Property level
//TODO static Utils class to make it easier to use!
//TODO support write-only properties?
//TODO support chain calls?

public class Bean<B>
{
	// TODO cache synchronization?
	@SuppressWarnings("unchecked")
	public static <B> Bean<B> create(Class<B> clazz)
	{
		Bean<B> helper = (Bean<B>) _cache.get(clazz);
		if (helper == null)
		{
			helper = new Bean<B>(clazz);
			_cache.put(clazz, helper);
		}
		return helper;
	}

	protected Bean(Class<B> clazz)
	{
		_clazz = clazz;
		_properties = ReflectUtils.getBeanProperties(_clazz);
		_mockInterceptor = new MockInterceptor(_properties);

		// Create a mock immediately with cglib
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(_mockInterceptor);

		_mock = clazz.cast(enhancer.create());
	}

	public Class<B> type()
	{
		return _clazz;
	}

	// Used in conjunction with property() method (as argument)
	//TODO find a better name for it?
	public B mock()
	{
		return _mock;
	}

	// Returns a beans property reference without using its hard-coded string
	// name
	// This pattern will always survive bean refactoring (compile-safe)!
	// The returned reference can be used to get/set property value or get its
	// name (in a safe way)
	public <U> Property<B, U> property(U mockCall)
	{
		PropertyDescriptor property = _mockInterceptor.lastUsedProperty();
		checkType(mockCall, property);
		return PropertyImpl.create(property);
	}

	// Create a new bean that delegates to this one but makes all its properties
	// bound
	// TODO use T & ChangeListenerAdapter as returned type?
	public B proxy(B source)
	{
		// Create a proxy with cglib
		Enhancer enhancer = new Enhancer();

		enhancer.setSuperclass(_clazz);
		enhancer.setInterfaces(new Class[] { ChangeListenerAdapter.class,
				ProxySource.class });
		enhancer.setCallback(new ProxyInterceptor<B>(source,
				_properties));
		return _clazz.cast(enhancer.create());
	}

	@SuppressWarnings("unchecked")
	public B source(B proxy)
	{
		if (proxy instanceof ProxySource)
		{
			Object source = ((ProxySource) proxy).source();
			if (_clazz.isAssignableFrom(source.getClass()))
			{
				return (B) source;
			}
		}
		return proxy;
	}

	private void checkType(Object property, PropertyDescriptor descriptor)
	{
		String message = null;
		// Check that a method has been called on _mock
		if (descriptor == null)
		{
			message = "No getter was called on mock()!";
		}
		// Check that last call returned the right type
		else if (property != null)
		{
			Class<?> expected = descriptor.getPropertyType();
			Class<?> actual = property.getClass();
			if (expected.isPrimitive())
			{
				expected = WRAPPERS.get(expected);
			}
			if (expected != actual)
			{
				message = "Getter called on mock() doesn't match passed argument!";
			}
		}
		else if (descriptor.getPropertyType().isPrimitive())
		{
			message = "Getter called on mock() doesn't match passed argument!";
		}

		// TODO pending cases: should make sure mock getters always return non
		// null!

		if (message != null)
		{
			// FIXME use better exception with better reasons
			throw new RuntimeException(message);
		}
	}

	// TODO Remove WRAPPERS with cglib util!
	// Used for making primitive class and their wrapepr classes equal
	// during comparison
	private static final Map<Class<?>, Class<?>> WRAPPERS = new HashMap<Class<?>, Class<?>>();

	static
	{
		WRAPPERS.put(byte.class, Byte.class);
		WRAPPERS.put(short.class, Short.class);
		WRAPPERS.put(char.class, Character.class);
		WRAPPERS.put(int.class, Integer.class);
		WRAPPERS.put(long.class, Long.class);
		WRAPPERS.put(float.class, Float.class);
		WRAPPERS.put(double.class, Double.class);
		WRAPPERS.put(boolean.class, Boolean.class);
	}

	// Global cache of ObjectHelper objects (each class T should have only one
	// ObjectHelper<T> instance)
	private static final Map<Class<?>, Bean<?>> _cache = new HashMap<Class<?>, Bean<?>>();

	private final Class<B> _clazz;
	private final PropertyDescriptor[] _properties;
	private final MockInterceptor _mockInterceptor;
	private final B _mock;
}
