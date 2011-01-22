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

package net.guts.gui.util;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.TypeLiteral;

/**
 * Special {@code Map} class that can store any type of {@code Object}s but still
 * ensures type-safety of all {@code get} methods, and without need for casting.
 *
 * @author Jean-Francois Poilpret
 */
//TODO Put into guts-common instead
public class TypeSafeMap
{
	public void putAll(TypeSafeMap map, boolean replaceWhenKeyExists)
	{
		if (replaceWhenKeyExists)
		{
			_map.putAll(map._map);
		}
		else
		{
			for (Map.Entry<String, Value> entry: map._map.entrySet())
			{
				if (!_map.containsKey(entry.getKey()))
				{
					_map.put(entry.getKey(), entry.getValue());
				}
			}
		}
	}
	
	public <T> void put(String key, TypeLiteral<T> type, T value)
	{
		_map.put(key, new Value(type, value));
	}
	
	public <T> void put(TypeLiteral<T> type, T value)
	{
		put(type.toString(), type, value);
	}
	
	public <T> void put(String key, Class<T> type, T value)
	{
		put(key, TypeLiteral.get(type), value);
	}
	
	public <T> void put(Class<T> type, T value)
	{
		put(type.getName(), type, value);
	}
	
	public <T> T get(TypeLiteral<T> type)
	{
		return get(type.toString(), type);
	}

	@SuppressWarnings("unchecked") 
	public <T> T get(String key, TypeLiteral<T> type)
	{
		Value value = _map.get(key);
		if (value != null && type.equals(value._type))
		{
			return (T) value._value;
		}
		else
		{
			return null;
		}
	}

	public <T> T get(Class<T> type)
	{
		return get(type.getName(), type);
	}

	public <T> T get(String key, Class<T> type)
	{
		return get(key, TypeLiteral.get(type));
	}
	
	static private class Value
	{
		Value(TypeLiteral<?> type, Object value)
		{
			_type = type;
			_value = value;
		}
		
		final private TypeLiteral<?> _type;
		final private Object _value;
	}
	
	final private Map<String, Value> _map = new HashMap<String, Value>();
}
