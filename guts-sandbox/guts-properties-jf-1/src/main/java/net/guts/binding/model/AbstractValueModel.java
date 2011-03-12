package net.guts.binding.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractValueModel<T> implements ValueModel<T>
{
	@Override public void addChangeListener(ValueChangeListener<? super T> listener)
	{
		_listeners.add(listener);
	}

	@Override public void removeChangeListener(ValueChangeListener<? super T> listener)
	{
		_listeners.remove(listener);
	}

	final protected void firePropertyChange()
	{
		for (ValueChangeListener<? super T> listener: _listeners)
		{
			listener.valueChanged(this);
		}
	}

	final private List<ValueChangeListener<? super T>> _listeners = 
		new ArrayList<ValueChangeListener<? super T>>();
}
