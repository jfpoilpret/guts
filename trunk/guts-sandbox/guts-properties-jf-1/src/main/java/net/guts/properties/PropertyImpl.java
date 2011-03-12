package net.guts.properties;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import com.google.inject.TypeLiteral;

//TODO factory (fluent API and typesafe) for PropertyImpl
class PropertyImpl<B, T> implements Property<B, T>
{
	static <B, T> Property<B, T> create(PropertyDescriptor descriptor)
	{
		return new PropertyImpl<B, T>(descriptor);
	}

	PropertyImpl(PropertyDescriptor descriptor)
	{
		_descriptor = descriptor;
	}

	@SuppressWarnings("unchecked") 
	@Override public TypeLiteral<T> type()
	{
		return (TypeLiteral<T>) TypeLiteral.get(_descriptor.getPropertyType());
	}

	@SuppressWarnings("unchecked")
	@Override public T get(B bean)
	{
		try
		{
			return (T) _descriptor.getReadMethod().invoke(bean);
		}
		catch (Exception e)
		{
			throw convert(e);
		}
	}

	@Override public void set(B bean, T value)
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

	@Override public String name()
	{
		return _descriptor.getName();
	}

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
			return new RuntimeException(e);
		}
	}

	private final PropertyDescriptor _descriptor;
}
