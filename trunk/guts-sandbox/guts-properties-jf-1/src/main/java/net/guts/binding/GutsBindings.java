package net.guts.binding;

import javax.swing.JComponent;

import net.guts.properties.Bean;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.ValueModel;

public class GutsBindings extends Bindings
{
	// Prevent direct instantiation
	protected GutsBindings()
	{
	}
	
	// Usage: bind(field, Models.of(JTextField.class).getText(), textValueModel);
	@SuppressWarnings("unchecked") 
	public static <C extends JComponent, T> void bind(
		C component, T getter, ValueModel<T> valueModel)
	{
		Bean<C> helper = Bean.create((Class<C>) component.getClass());
		bind(component, helper.property(getter).name(), valueModel);
	}
}
