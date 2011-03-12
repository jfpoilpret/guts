package net.guts.binding.model;

public interface ValueModel<T>
{	
	public T get();
	public void set(T value);
	
	public void addChangeListener(ValueChangeListener<? super T> listener);
	public void removeChangeListener(ValueChangeListener<? super T> listener);
}
