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

/**
 * Special {@code Map} class that can store any type of {@code Object}s but still
 * ensures type-safety of all {@code get} methods, and without need for casting.
 *
 * @author Jean-Francois Poilpret
 */
//TODO Put into guts-common instead
public class TypeSafeMap
{
	public <T> void put(String key, Class<T> type, T value)
	{
		_map.put(key, new Value(type, value));
	}
	
	public <T> void put(Class<T> type, T value)
	{
		put(type.getName(), type, value);
	}
	
	public <T> T get(Class<T> type)
	{
		return get(type.getName(), type);
	}

	@SuppressWarnings("unchecked") 
	public <T> T get(String key, Class<T> type)
	{
		Value value = _map.get(key);
		if (value != null && type == value._type)
		{
			return (T) value._value;
		}
		else
		{
			return null;
		}
	}
	
	static private class Value
	{
		Value(Class<?> type, Object value)
		{
			_type = type;
			_value = value;
		}
		
		final private Class<?> _type;
		final private Object _value;
	}
	
	final private Map<String, Value> _map = new HashMap<String, Value>();
}
