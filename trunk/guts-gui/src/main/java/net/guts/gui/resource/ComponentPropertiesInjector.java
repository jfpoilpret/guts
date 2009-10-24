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

import net.guts.common.bean.UntypedProperty;

class ComponentPropertiesInjector extends AbstractComponentInjector<Component>
{
	@Override public void inject(Component component, ResourceMap resources)
	{
		String prefix = prefix(component);
		if (prefix == null)
		{
			return;
		}
		Class<? extends Component> componentType = component.getClass();
		// For each injectable resource
		for (ResourceMap.Key key: resources.keys(prefix))
		{
			// Check that this property exists
			UntypedProperty property = writableProperty(key.key(), componentType);
			if (property == null)
			{
				continue;
			}
			Class<?> type = property.type();
			// Get the value in the correct type
			Object value = resources.getValue(key, type);
			// Special handling for mnemonics
			if ("text".equals(key.key()) && value instanceof String)
			{
				MnemonicInfo info = MnemonicInfo.extract((String) value);
				// Set the property with the resource value
				setProperty(component, "text", info.getText());
				setProperty(component, "displayedMnemonic", info.getMnemonic());
				setProperty(component, "displayedMnemonicIndex", info.getMnemonicIndex());
			}
			else
			{
				// Set the property with the resource value
				property.set(component, value);
			}
		}
	}
}
