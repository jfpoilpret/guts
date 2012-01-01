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

import static net.guts.common.type.PrimitiveHelper.toWrapper;

import java.lang.reflect.AccessibleObject;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.google.inject.TypeLiteral;

abstract public class UntypedProperty
{
	UntypedProperty(String name, TypeLiteral<?> type, 
		AccessibleObject setter, AccessibleObject getter)
	{
		_name = name;
		_type = type;
		_setter = setter;
		_getter = getter;
	}

	final public TypeLiteral<?> type()
	{
		return _type;
	}

	final public boolean isReadable()
	{
		return _getter != null;
	}
	final public boolean isWritable()
	{
		return _setter != null;
	}

	static void setAccessible(final AccessibleObject object, final boolean accessible)
	{
		AccessController.doPrivileged(new PrivilegedAction<Void>()
			{
				@Override public Void run()
				{
					object.setAccessible(accessible);
					return null;
				}
			});
	}

	abstract void setValue(Object bean, Object value) throws Exception;
	abstract Object getValue(Object bean) throws Exception;

	//CSOFF: IllegalCatch
	public void set(Object bean, Object value)
	{
		// Check that there is a setter!
		if (!isWritable())
		{
			throw new PropertyException(
				String.format("Property `%s` in bean `%s` is not writable",
					_name, bean.getClass()));
		}
		
		if (	value == null
			||	toWrapper(type().getRawType()).isAssignableFrom(value.getClass()))
		{
			boolean accessible = _setter.isAccessible();
			if (!accessible)
			{
				setAccessible(_setter, true);
			}
			try
			{
				setValue(bean, value);
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
		else
		{
			throw new PropertyException(
				String.format("Cannot set value of type `%s` to property `%s` in bean `%s`",
					value.getClass(), _name, bean.getClass()));
			
		}
	}
	//CSON: IllegalCatch

	//CSOFF: IllegalCatch
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
			return getValue(bean);
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
	//CSON: IllegalCatch

	private final String _name;
	private final TypeLiteral<?> _type;
	private final AccessibleObject _getter;
	private final AccessibleObject _setter;
}
