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

package net.guts.gui.binding;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

final class PropertyHelper
{
	private PropertyHelper()
	{
	}
	
	// CSOFF: IllegalCatchCheck
	static public void set(Object bean, PropertyDescriptor property, Object value)
	{
		try
		{
			property.getWriteMethod().invoke(bean, value);
		}
		catch (Exception e)
		{
			throw convert(e);
		}
	}
	// CSON: IllegalCatchCheck
	
	// CSOFF: IllegalCatchCheck
	static public Object get(Object bean, PropertyDescriptor property)
	{
		try
		{
			return property.getReadMethod().invoke(bean);
		}
		catch (Exception e)
		{
			throw convert(e);
		}
	}
	// CSON: IllegalCatchCheck

	static private RuntimeException convert(Throwable e)
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
			e.printStackTrace();
			return new RuntimeException(e);
		}
	}
}
