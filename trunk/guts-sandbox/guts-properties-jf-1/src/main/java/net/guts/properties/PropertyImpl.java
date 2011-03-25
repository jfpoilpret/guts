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
import java.util.List;

import com.google.inject.TypeLiteral;

class PropertyImpl<B, T> implements Property<B, T>
{
	static <B, T> Property<B, T> create(List<PropertyDescriptor> descriptors)
	{
		return new PropertyImpl<B, T>(descriptors);
	}

	PropertyImpl(List<PropertyDescriptor> descriptors)
	{
		int last = descriptors.size() - 1;
		_descriptors = descriptors.subList(0, last);
		_lastDescriptor = descriptors.get(last);
	}

	@SuppressWarnings("unchecked") 
	@Override public TypeLiteral<T> type()
	{
		return (TypeLiteral<T>) TypeLiteral.get(_lastDescriptor.getPropertyType());
	}

	@SuppressWarnings("unchecked")
	@Override public T get(B bean)
	{
		Object current = bean;
		for (PropertyDescriptor property: _descriptors)
		{
			Object next = get(property, current);
			current = next;
		}
		return (T) get(_lastDescriptor, current);
	}
	
	//CSOFF: IllegalCatch
	static private Object get(PropertyDescriptor property, Object bean)
	{
		try
		{
			return property.getReadMethod().invoke(bean);
		}
		catch (Exception e)
		{
			throw BeanHelper.convert(e);
		}
	}
	//CSON: IllegalCatch

	//CSOFF: IllegalCatch
	@Override public void set(B bean, T value)
	{
		Object current = bean;
		for (PropertyDescriptor property: _descriptors)
		{
			Object next = get(property, current);
			current = next;
		}
		try
		{
			_lastDescriptor.getWriteMethod().invoke(current, value);
		}
		catch (Exception e)
		{
			throw BeanHelper.convert(e);
		}
	}
	//CSON: IllegalCatch

	@Override public String name()
	{
		StringBuilder name = new StringBuilder();
		for (PropertyDescriptor property: _descriptors)
		{
			name.append(property.getName());
			name.append('.');
		}
		name.append(_lastDescriptor.getName());
		return name.toString();
	}

	private final List<PropertyDescriptor> _descriptors;
	private final PropertyDescriptor _lastDescriptor;
}
