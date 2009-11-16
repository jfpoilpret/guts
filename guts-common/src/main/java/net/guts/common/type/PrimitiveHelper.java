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

package net.guts.common.type;

public final class PrimitiveHelper
{
	private PrimitiveHelper()
	{
	}
	
	@SuppressWarnings("unchecked")
	static public <T> Class<T> toPrimitive(Class<T> clazz)
	{
		if (clazz == Integer.class)
		{
			return (Class<T>) int.class;
		}
		else if (clazz == Short.class)
		{
			return (Class<T>) short.class;
		}
		else if (clazz == Byte.class)
		{
			return (Class<T>) byte.class;
		}
		else if (clazz == Long.class)
		{
			return (Class<T>) long.class;
		}
		else if (clazz == Character.class)
		{
			return (Class<T>) char.class;
		}
		else if (clazz == Float.class)
		{
			return (Class<T>) float.class;
		}
		else if (clazz == Double.class)
		{
			return (Class<T>) double.class;
		}
		else if (clazz == Boolean.class)
		{
			return (Class<T>) boolean.class;
		}
		else
		{
			return clazz;
		}
	}
	
	@SuppressWarnings("unchecked")
	static public <T> Class<T> toWrapper(Class<T> clazz)
	{
		if (clazz == int.class)
		{
			return (Class<T>) Integer.class;
		}
		else if (clazz == short.class)
		{
			return (Class<T>) Short.class;
		}
		else if (clazz == byte.class)
		{
			return (Class<T>) Byte.class;
		}
		else if (clazz == long.class)
		{
			return (Class<T>) Long.class;
		}
		else if (clazz == char.class)
		{
			return (Class<T>) Character.class;
		}
		else if (clazz == float.class)
		{
			return (Class<T>) Float.class;
		}
		else if (clazz == double.class)
		{
			return (Class<T>) Double.class;
		}
		else if (clazz == boolean.class)
		{
			return (Class<T>) Boolean.class;
		}
		else
		{
			return clazz;
		}
	}
}
