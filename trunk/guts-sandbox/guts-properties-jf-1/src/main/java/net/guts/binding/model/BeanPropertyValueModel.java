package net.guts.binding.model;

import net.guts.properties.Property;

//TODO how to handle null values when T is primitive type actually?
class BeanPropertyValueModel<B, T> extends AbstractValueModel<T>
{
	BeanPropertyValueModel(B bean, Property<B, T> property)
	{
		_bean = bean;
		_property = property;
	}
	
	@Override public T get()
	{
		return _property.get(_bean);
	}
	
	@Override public void set(T value)
	{
		_property.set(_bean, value);
		firePropertyChange();
	}

	final private B _bean;
	final private Property<B, T> _property;
}
