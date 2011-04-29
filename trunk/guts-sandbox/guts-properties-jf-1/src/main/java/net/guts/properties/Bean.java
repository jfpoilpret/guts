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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;

public class Bean<B>
{
	@SuppressWarnings("unchecked")
	synchronized public static <B> Bean<B> create(Class<B> clazz)
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
		MockInterceptor mockInterceptor = new MockInterceptor(_properties);

		// Create a mock immediately with cglib
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(mockInterceptor);
		enhancer.setInterceptDuringConstruction(false);

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

	//TODO how can we support getters that return collections and then 
	//calls on those collections?
	// Returns a beans property reference without using its hard-coded string
	// name
	// This pattern will always survive bean refactoring (compile-safe)!
	// The returned reference can be used to get/set property value or get its
	// name (in a safe way)
	public <U> Property<B, U> property(U mockCall)
	{
		List<PropertyDescriptor> properties = RecordedGetters.calledProperties();
		checkType(mockCall, properties);
		return PropertyImpl.create(properties);
	}

	// Create a new bean that delegates to this one but makes all its properties
	// bound
	public B proxy(B source)
	{
		// Create a proxy with cglib
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(_clazz);
		enhancer.setInterfaces(new Class[] { ChangeListenerAdapter.class, ProxySource.class });
		enhancer.setCallback(new ProxyInterceptor<B>(source, _properties));
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

	private void checkType(Object property, List<PropertyDescriptor> descriptors)
	{
		// Check that a method has been called on _mock
		if (descriptors.isEmpty())
		{
			throw new IllegalStateException("No getter called on any bean mock()!");
		}

		// Check that last call returned the right type
		PropertyDescriptor lastDescriptor = descriptors.get(descriptors.size() - 1);
		if (property != null)
		{
			Class<?> expected = BeanHelper.toWrapper(lastDescriptor.getPropertyType());
			Class<?> actual = property.getClass();
			// Either:
			// - both types are the same 
			// - or the actual type is a mock of the expected type
			if (	expected != actual
				&&	!(		property instanceof Factory
						&&	((Factory) property).getCallback(0) instanceof MockInterceptor
						&&	expected == actual.getSuperclass()))
			{
				throw new IllegalStateException(
					"Last getter called on mock() doesn't match passed argument!");
			}
		}
		else if (lastDescriptor.getPropertyType().isPrimitive())
		{
			throw new IllegalStateException(
				"Last getter called on mock() doesn't match passed argument!");
		}

		// Check all chained types match
		Iterator<PropertyDescriptor> i = descriptors.iterator();
		PropertyDescriptor current = i.next();
		while (i.hasNext())
		{
			PropertyDescriptor next = i.next();
			Class<?> expected = BeanHelper.toWrapper(current.getPropertyType());
			Class<?> actual = next.getReadMethod().getDeclaringClass();
			if (expected != actual)
			{
				throw new IllegalStateException(
					"Chained getter called on mock() doesn't match passed argument!");
			}
			
			current = next;
		}
	}
	
	// Global cache of Bean instances (each class B should have only one
	// Bean<B> instance)
	private static final Map<Class<?>, Bean<?>> _cache = new HashMap<Class<?>, Bean<?>>();

	private final Class<B> _clazz;
	private final PropertyDescriptor[] _properties;
	private final B _mock;
}
