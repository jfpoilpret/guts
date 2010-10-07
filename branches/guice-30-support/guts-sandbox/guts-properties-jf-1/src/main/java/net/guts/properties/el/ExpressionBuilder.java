package net.guts.properties.el;

public class ExpressionBuilder<T>
{
	private ExpressionBuilder(ExpressionSource<T> expression)
	{
		_source = expression;
	}

	static public <T> ExpressionBuilder<T> expression(ExpressionSource<T> expression)
	{
		return new ExpressionBuilder<T>(expression);
	}
	
	public <U> ExpressionBuilder<T> addVariable(String name, Class<U> type)
	{
		return addVariable(name, type, null);
	}
	
	public <U> ExpressionBuilder<T> addVariable(String name, Class<U> type, U value)
	{
		if (value == null)
		{
			//TODO Create a proxy for the value?
			
		}
		_context.setVariable(name, value, type);
		return this;
	}
	
	//TODO make the builder unusable (throw Exception) after first call to build()
	public Expression<T> build()
	{
		// Check the expression works first!
		try
		{
			_source.evaluate(_context);
			return new ExpressionImpl<T>(_source, _context);
		}
		catch (Exception e)
		{
			//TODO What to do here?
			return null;
		}
	}
	
	final private ExpressionSource<T> _source;
	final private ExpressionContext _context = new ExpressionContext();
}
