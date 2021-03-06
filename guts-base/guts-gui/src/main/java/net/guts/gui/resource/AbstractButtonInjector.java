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

import javax.swing.AbstractButton;

import net.guts.gui.resource.ResourceMap.Key;

/**
 * Features of this injector are documented in {@link ResourceModule}.
 */
//CSOFF: AbstractClassName
class AbstractButtonInjector extends BeanPropertiesInjector<AbstractButton>
{
	@Override protected boolean handleSpecialProperty(
		AbstractButton button, Key key, ResourceMap resources)
	{
		// Special handling for mnemonics
		if ("text".equals(key.name()))
		{
			String value = resources.getValue(key, String.class);
			MnemonicInfo info = MnemonicInfo.extract(value);
			// Set the property with the resource value
			button.setText(info.getText());
			button.setMnemonic(info.getMnemonic());
			button.setDisplayedMnemonicIndex(info.getMnemonicIndex());
			return true;
		}
		return false;
	}
}
//CSON: AbstractClassName
