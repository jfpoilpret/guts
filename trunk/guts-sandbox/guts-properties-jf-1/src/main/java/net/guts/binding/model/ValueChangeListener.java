package net.guts.binding.model;

public interface ValueChangeListener<T>
{	
	public void valueChanged(ValueModel<? extends T> model);
}
