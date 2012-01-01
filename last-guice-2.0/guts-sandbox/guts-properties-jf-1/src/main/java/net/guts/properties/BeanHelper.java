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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

final class BeanHelper
{	
	private BeanHelper()
	{
	}

	static Class<?> toWrapper(Class<?> source)
	{
		return (source.isPrimitive() ? WRAPPERS.get(source) : source);
	}
	
	static RuntimeException convert(Throwable e)
	{
		if (e instanceof InvocationTargetException)
		{
			return convert(((InvocationTargetException) e).getTargetException());
		}
		else if (e instanceof RuntimeException)
		{
			return (RuntimeException) e;
		}
		else
		{
			return new RuntimeException(e);
		}
	}

	// Used for making primitive class and their wrapper classes equal
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
}
