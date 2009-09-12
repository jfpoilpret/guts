//  Copyright 2009 Jean-Francois Poilpret
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package net.guts.gui.binding;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.text.JTextComponent;

import com.google.inject.internal.cglib.core.ReflectUtils;
import com.google.inject.internal.cglib.proxy.Enhancer;

//TODO also support multi-level mocks, e.g parent.getChild().getName()!
//TODO write TC!
final public class SwingBinding<B>
{
	//TODO Implement some caching somehow?
	// because cglib enhancer creation must be costly if recreated everytime for the same bean class!
	static public <B> SwingBinding<B> create(Class<B> bean)
	{
		return new SwingBinding<B>(bean);
	}
	
	private SwingBinding(Class<B> bean)
	{
		_bean = bean;
		_mockInterceptor = new MockInterceptor(ReflectUtils.getBeanProperties(_bean));

		// Create a mock immediately with cglib
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(_bean);
		enhancer.setCallback(_mockInterceptor);
		_mock = _bean.cast(enhancer.create());
	}
	
	public void guiToBean(B bean)
	{
		for (Binding binding: _bindings)
		{
			binding.guiToBean(bean);
		}
	}
	
	public void beanToGui(B bean)
	{
		for (Binding binding: _bindings)
		{
			binding.beanToGui(bean);
		}
	}
	
	public B binder()
	{
		return _mock;
	}
	
	// All bind() methods to Swing components
//	public <T extends JTextComponent, V> T bind(T component, V getter)
	public <T extends JTextComponent> T bind(T component, String getter)
	{
		PropertyDescriptor property = _mockInterceptor.lastUsedProperty();
		if (property == null)
		{
			//TODO exception!
		}
		//TODO Check that getter type is compatible with property type!
		//TODO check that component "text" property can be converted to/from getter type
		
		// Add binding to list of managed bindings!
		_bindings.add(new JTextComponentBinding(component, property));
		return component;
	}
	
	public <T extends JCheckBox> T bind(T component, Boolean getter)
	{
		PropertyDescriptor property = _mockInterceptor.lastUsedProperty();
		if (property == null)
		{
			//TODO exception!
		}
		//TODO Check that getter type is compatible with property type!
		//TODO check that component "selected" property can be converted to/from getter type
		
		// Add binding to list of managed bindings!
		_bindings.add(new JCheckBoxBinding(component, property));
		return component;
	}
	
	public <T extends JSlider> T bind(T component, Integer getter)
	{
		PropertyDescriptor property = _mockInterceptor.lastUsedProperty();
		if (property == null)
		{
			//TODO exception!
		}
		//TODO Check that getter type is compatible with property type!
		//TODO check that component "selected" property can be converted to/from getter type
		
		// Add binding to list of managed bindings!
		_bindings.add(new JSliderBinding(component, property));
		return component;
	}
	
	public <T extends JComboBox, V> T bind(T component, V getter)
	{
		PropertyDescriptor property = _mockInterceptor.lastUsedProperty();
		if (property == null)
		{
			//TODO exception!
		}
		//TODO Check that getter type is compatible with property type!
		//TODO check that component "selected" property can be converted to/from getter type
		
		// Add binding to list of managed bindings!
		_bindings.add(new JComboBoxSelectionBinding(component, property));
		return component;
	}
	
	private final Class<B> _bean;
	private final MockInterceptor _mockInterceptor;
	private final B _mock;
	private final List<Binding> _bindings = new ArrayList<Binding>();
}
