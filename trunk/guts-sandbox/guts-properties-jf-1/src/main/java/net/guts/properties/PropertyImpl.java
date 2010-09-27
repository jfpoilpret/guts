package net.guts.properties;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

public class PropertyImpl<T, U>
{
	static<T, U> PropertyImpl<T, U> create(PropertyDescriptor descriptor)
	{
		return new PropertyImpl<T, U>(descriptor);
	}
	
	protected PropertyImpl(PropertyDescriptor descriptor)
	{
		_descriptor = descriptor;
	}
	
	public Class<?> type()
	{
		return _descriptor.getPropertyType();
	}
	
	@SuppressWarnings("unchecked")
	public U get(T bean)
	{
		try
		{
			return (U) _descriptor.getReadMethod().invoke(bean);
		}
		catch (Exception e)
		{
			throw convert(e);
		}
	}
	
	public void set(T bean, U value)
	{
		try
		{
			_descriptor.getWriteMethod().invoke(bean, value);
		}
		catch (Exception e)
		{
			throw convert(e);
		}
	}
	
	public String name()
	{
		return _descriptor.getName();
	}

	//TODO Methods for adding/removing PCL for this variable? Maybe not needed here?
	// Binding outside or inside?
	
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
	
	private final PropertyDescriptor _descriptor;
}
