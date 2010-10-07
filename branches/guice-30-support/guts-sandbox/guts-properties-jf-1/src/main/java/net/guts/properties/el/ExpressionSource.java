package net.guts.properties.el;

public interface ExpressionSource<T>
{
	public T evaluate(ExpressionContext context);
}
