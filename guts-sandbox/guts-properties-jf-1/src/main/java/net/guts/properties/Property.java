package net.guts.properties;

import com.google.inject.TypeLiteral;

public interface Property<B, T>
{
	public String name();
	public TypeLiteral<T> type();
	public T get(B bean);
	public void set(B bean, T value);

	// public void addPropertyChangeListener(B bean, PropertyChangeListener
	// listener);
	// public void removePropertyChangeListener(B bean, PropertyChangeListener
	// listener);
}
