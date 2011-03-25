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
