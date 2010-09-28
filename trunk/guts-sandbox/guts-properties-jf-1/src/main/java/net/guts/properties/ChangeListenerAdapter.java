package net.guts.properties;

import java.beans.PropertyChangeListener;

public interface ChangeListenerAdapter

{
	public void addPropertyChangeListener(PropertyChangeListener listener);

	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener);

	public void removePropertyChangeListener(PropertyChangeListener listener);

	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener);

}
