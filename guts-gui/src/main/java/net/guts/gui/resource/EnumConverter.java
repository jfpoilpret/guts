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

package net.guts.gui.resource;

/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
public class EnumConverter<T extends Enum<T>> implements ResourceConverter<T>
{
	public EnumConverter(Class<T> enumType)
	{
		_enumValues = enumType.getEnumConstants();
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.resource.ResourceConverter#convert(java.lang.String)
	 */
	@Override public T convert(ResourceEntry value)
	{
		for (T enumValue: _enumValues)
		{
			if (enumValue.name().equals(value.value()))
			{
				return enumValue;
			}
		}
		return null;
	}

	final private T[] _enumValues;
}
