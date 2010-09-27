package net.guts.properties;

public interface ExpressionSource<T>
{
	public T evaluate(ExpressionContext context);
}
