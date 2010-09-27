package net.guts.properties;

public interface Expression<T>
{
	public T evaluate();
	public Expression<T> set(String name, Object value);
}
