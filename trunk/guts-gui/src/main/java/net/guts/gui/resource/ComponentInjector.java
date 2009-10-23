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

package net.guts.gui.resource;

import java.awt.Component;
import java.awt.event.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.bean.UntypedProperty;
import net.guts.common.bean.UntypedPropertyFactory;
import static net.guts.common.type.PrimitiveHelper.*;

import com.google.inject.Inject;


//TODO find a better name like SingleResourceInjector? InstanceResourcesInjector?...
/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
public interface ComponentInjector<T>
{
	// resources is the list of all resources (strongly typed) for this
	// component
	// Implementations of this method should iterate through resources and
	// inject each individual resource into component
	public void inject(T component, ResourceMap resources);
}

// Default component injector
class BeanPropertiesInjector implements ComponentInjector<Component>
{
	static final private Logger _logger = 
		LoggerFactory.getLogger(BeanPropertiesInjector.class);

	@Inject BeanPropertiesInjector(UntypedPropertyFactory properties)
	{
		_properties = properties;
	}
	
	@Override public void inject(Component component, ResourceMap resources)
	{
		String prefix = component.getName();
		Class<? extends Component> componentType = component.getClass();
		if (prefix == null)
		{
			String msg = String.format("Component has no name: %d", component);
			_logger.info(msg);
			return;
		}
		// For each injectable resource
		for (ResourceMap.Key key: resources.keys(prefix))
		{
			// Use FEST-Reflect & Check that this property exists
			UntypedProperty property = _properties.property(key.key(), componentType);
			if (property == null || !property.isWritable())
			{
				String msg = String.format("Unknown property `%s` of type `%s`", 
					key.key(), componentType);
				_logger.info(msg);
				continue;
			}
			Class<?> type = property.type();
			// Get the value in the correct type
			Object value = resources.getValue(key, type);
			// Special handling for mnemonics
			if ("text".equals(key.key()) && value instanceof String)
			{
				MnemonicInfo info = MnemonicInfo.extract((String) value);
				if (info != null)
				{
					// Set the property with the resource value
					setProperty(component, "text", info.getText());
					setProperty(component, "displayedMnemonic", info.getMnemonic());
					setProperty(component, "displayedMnemonicIndex", info.getMnemonicIndex());
				}
				else
				{
					// Set the property with the resource value
					setProperty(component, "text", (String) value);
					setProperty(component, "displayedMnemonic", KeyEvent.VK_UNDEFINED);
					setProperty(component, "displayedMnemonicIndex", -1);
				}
			}
			else
			{
				// Set the property with the resource value
				property.set(component, value);
			}
		}
	}
	
	private void setProperty(Component component, String name, Object value)
	{
		Class<?> type = component.getClass();
		UntypedProperty property = _properties.property(name, type);
		if (property == null || !property.isWritable())
		{
			//TODO log something
			return;
		}
		if (value != null && !toWrapper(property.type()).isAssignableFrom(value.getClass()))
		{
			//TODO log something
			return;
		}
		property.set(component, value);
	}
	
	final private UntypedPropertyFactory _properties;
}
