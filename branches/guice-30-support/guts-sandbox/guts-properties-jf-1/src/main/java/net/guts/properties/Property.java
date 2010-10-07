package net.guts.properties;

import com.google.inject.TypeLiteral;

public interface Property<BeanType, FieldType> {

	public String name();

	public TypeLiteral<FieldType> type();

	public FieldType get(BeanType bean);

	public void set(BeanType bean, FieldType value);

	// public void addPropertyChangeListener(B bean, PropertyChangeListener
	// listener);

	// public void removePropertyChangeListener(B bean, PropertyChangeListener
	// listener);

}
