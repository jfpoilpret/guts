package net.guts.properties.el;

class ExpressionImpl<T> implements Expression<T>
{
	ExpressionImpl(ExpressionSource<T> source, ExpressionContext context)
	{
		_source = source;
		_context = context;
	}
	
	@Override public T evaluate()
	{
		return _source.evaluate(_context);
	}

	@Override public Expression<T> set(String name, Object value)
	{
		// TODO Auto-generated method stub
		// Check that there is a variable with this name in _context
		// Check that value is null or has an expected type in _context
		// If OK override variable in _context
		return this;
	}

	final private ExpressionSource<T> _source;
	final private ExpressionContext _context;
}
