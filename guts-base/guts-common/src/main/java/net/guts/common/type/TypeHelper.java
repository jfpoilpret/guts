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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.TypeLiteral;

public final class TypeHelper
{
	private TypeHelper()
	{
	}
	
	static public String getPackage(Class<?> type)
	{
		String pack = type.getName();
		return pack.substring(0, pack.lastIndexOf('.'));
	}
	
	static public String getPackagePath(Class<?> type)
	{
		return getPackage(type).replaceAll("\\.", "/");
	}
	
	//TODO also deal with interfaces?
	//CSOFF: ParameterAssignmentCheck
	static public <T> T findBestMatchInTypeHierarchy(Map<Class<?>, T> map, Class<?> type)
	{
		T value = null;
		while (true)
		{
			value = map.get(type);
			if (value != null)
			{
				return value;
			}
			type = type.getSuperclass();
			if (type == null)
			{
				return null;
			}
		}
	}
	//CSON: ParameterAssignmentCheck

	//TODO also deal with interfaces?
	//CSOFF: ParameterAssignmentCheck
	static public <T> List<T> findAllMatchesInTypeHierarchy(
		Map<Class<?>, T> map, Class<?> type)
	{
		List<T> values = new ArrayList<T>();
		while (true)
		{
			T value = map.get(type);
			if (value != null)
			{
				values.add(value);
			}
			type = type.getSuperclass();
			if (type == null)
			{
				return values;
			}
		}
	}
	//CSON: ParameterAssignmentCheck

	static public boolean typeIsSubtypeOf(
		TypeLiteral<?> subtype, TypeLiteral<?> supertype)
	{
		// First check that raw types are compatible
		// Then check that generic types are compatible! HOW????
		return (	subtype.equals(supertype)
			||	(	supertype.getRawType().isAssignableFrom(subtype.getRawType())
				&&	supertype.equals(subtype.getSupertype(supertype.getRawType()))));
	}
}
