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

package net.guts.common.bean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static net.guts.common.type.PrimitiveHelper.*;

//TODO Later move this class to guts-common!

/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
public class UntypedProperty
{
	UntypedProperty(PropertyDescriptor descriptor)
	{
		_descriptor = descriptor;
		_name = descriptor.getName();
		_setter = descriptor.getWriteMethod();
		_getter = descriptor.getReadMethod();
	}

	public Class<?> type()
	{
		return _descriptor.getPropertyType();
	}
	
	public boolean isReadable()
	{
		return _getter != null;
	}
	
	public boolean isWritable()
	{
		return _setter != null;
	}
	
	public void set(Object bean, Object value)
	{
		if (value == null)
		{
			setValue(bean, null);
		}
		//FIXME need to care also for primitive Vs wrapper types!
		else if (toWrapper(type()).isAssignableFrom(value.getClass()))
		{
			setValue(bean, value);
		}
		else
		{
			throw new PropertyException(
				String.format("Cannot set value of type `%s` to property `%s` in bean `%s`",
					value.getClass(), _name, bean.getClass()));
			
		}
	}
	
	protected void setValue(Object bean, Object value)
	{
		// Check that there is a setter!
		if (!isWritable())
		{
			throw new PropertyException(
				String.format("Property `%s` in bean `%s` is not writable",
					_name, bean.getClass()));
		}
		// Make setter available
		boolean accessible = _setter.isAccessible();
		if (!accessible)
		{
			setAccessible(_setter, true);
		}
		try
		{
			_setter.invoke(bean, value);
		}
		catch (Exception e)
		{
			throw new PropertyException(
				String.format("Could not write value `%s` into property `%s` of bean `%s`",
					value, _name, bean.getClass()), e);
		}
		finally
		{
			if (!accessible)
			{
				setAccessible(_setter, false);
			}
		}
	}
	
	public Object get(Object bean)
	{
		// Check that there is a getter!
		if (!isReadable())
		{
			throw new PropertyException(
				String.format("Property `%s` in bean `%s` is not readable",
					_name, bean.getClass()));
		}
		// Make getter available
		boolean accessible = _getter.isAccessible();
		if (!accessible)
		{
			setAccessible(_getter, true);
		}
		try
		{
			return _getter.invoke(bean);
		}
		catch (Exception e)
		{
			throw new PropertyException(
				String.format("Could not read value of property `%s` of bean `%s`",
					_name, bean.getClass()), e);
		}
		finally
		{
			if (!accessible)
			{
				setAccessible(_getter, false);
			}
		}
	}
	
	static private void setAccessible(final Method method, final boolean accessible)
	{
		AccessController.doPrivileged(new PrivilegedAction<Void>()
			{
				@Override public Void run()
				{
					method.setAccessible(accessible);
					return null;
				}
			});
	}

	final private PropertyDescriptor _descriptor;
	final private String _name;
	final private Method _setter;
	final private Method _getter;
}