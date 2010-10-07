package net.guts.properties.el;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.TypeLiteral;

public final class ExpressionContext
{
	ExpressionContext()
	{
	}
	
	public <T> T getVariable(String name, Class<T> clazz)
	{
		TypeLiteral<T> type = TypeLiteral.get(clazz);
		return getVariable(name, type);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getVariable(String name, TypeLiteral<T> type)
	{
		Variable variable = _variables.get(name);
		if (	variable != null
			&&	typeIsCompatible(variable.getType(), type))
		{
			return (T) variable.getValue();
		}
		else
		{
			return null;
		}
	}
	
	private boolean typeIsCompatible(TypeLiteral<?> variableType, TypeLiteral<?> requiredType)
	{
		// TODO Maybe be less strict for compatibility check and allow requiredType to be
		// a supertype of variableType?
		return variableType.equals(requiredType);
	}
	
	<T> void setVariable(String name, T value, Class<T> type)
	{
		_variables.put(name, new Variable(value, TypeLiteral.get(type)));
	}
	
	<T> void setVariable(String name, T value, TypeLiteral<T> type)
	{
		_variables.put(name, new Variable(value, type));
	}
	
	static final private class Variable
	{
		Variable(Object value, TypeLiteral<?> type)
		{
			_value = value;
			_type = type;
		}
		
		public TypeLiteral<?> getType()
		{
			return _type;
		}
		
		public Object getValue()
		{
			return _value;
		}
		
		final private TypeLiteral<?> _type;
		final private Object _value;
	}
	final private Map<String, Variable> _variables = new HashMap<String, Variable>();
}
