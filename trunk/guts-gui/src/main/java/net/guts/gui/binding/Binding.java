package net.guts.gui.binding;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JSlider;
import javax.swing.ListModel;
import javax.swing.text.JTextComponent;

interface Binding
{
	public void guiToBean(Object bean);
	public void beanToGui(Object bean);
}

abstract class AbstractBinding implements Binding
{
	protected AbstractBinding(PropertyDescriptor property)
	{
		_property = property;
	}
	
	protected <T> void valueToBean(T value, Object bean)
	{
		PropertyHelper.set(bean, _property, value);
	}
	
	protected <T> T beanToValue(Object bean)
	{
		return (T) PropertyHelper.get(bean, _property);
	}
	
	final private PropertyDescriptor _property;
}

class JTextComponentBinding extends AbstractBinding
{
	public JTextComponentBinding(JTextComponent component, PropertyDescriptor property)
	{
		super(property);
		_component = component;
	}
	
	public void guiToBean(Object bean)
	{
		valueToBean(_component.getText(), bean);
	}
	
	public void beanToGui(Object bean)
	{
		_component.setText((String) beanToValue(bean));
	}
	
	final private JTextComponent _component;
}

class JCheckBoxBinding extends AbstractBinding
{
	public JCheckBoxBinding(JCheckBox component, PropertyDescriptor property)
	{
		super(property);
		_component = component;
	}
	
	public void guiToBean(Object bean)
	{
		valueToBean(_component.isSelected(), bean);
	}
	
	public void beanToGui(Object bean)
	{
		_component.setSelected((Boolean) beanToValue(bean));
	}
	
	final private JCheckBox _component;
}

class JSliderBinding extends AbstractBinding
{
	public JSliderBinding(JSlider component, PropertyDescriptor property)
	{
		super(property);
		_component = component;
	}
	
	public void guiToBean(Object bean)
	{
		valueToBean(_component.getValue(), bean);
	}
	
	public void beanToGui(Object bean)
	{
		_component.setValue((Integer) beanToValue(bean));
	}
	
	final private JSlider _component;
}

class JComboBoxSelectionBinding extends AbstractBinding
{
	public JComboBoxSelectionBinding(JComboBox component, PropertyDescriptor property)
	{
		super(property);
		_component = component;
	}
	
	public void guiToBean(Object bean)
	{
		valueToBean(_component.getSelectedItem(), bean);
	}
	
	public void beanToGui(Object bean)
	{
		_component.setSelectedItem(beanToValue(bean));
	}
	
	final private JComboBox _component;
}

//TODO property could be either List or Set...
class JListContentBinding extends AbstractBinding
{
	public JListContentBinding(JList component, PropertyDescriptor property)
	{
		super(property);
		_component = component;
	}
	
	public void guiToBean(Object bean)
	{
		ListModel model = _component.getModel();
		List<Object> content = new ArrayList<Object>();
//		valueToBean(_component.getModel()., bean);
	}
	
	public void beanToGui(Object bean)
	{
//		_component.setSelectedItem(beanToValue(bean));
	}
	
	final private JList _component;
}

