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

import javax.swing.JLabel;

import net.guts.gui.resource.ResourceMap.Key;

class JLabelInjector extends BeanPropertiesInjector<JLabel>
{
	@Override protected boolean handleSpecialProperty(
		JLabel label, Key key, ResourceMap resources)
	{
		// Special handling for mnemonics
		if ("text".equals(key.key()))
		{
			String value = resources.getValue(key, String.class);
			MnemonicInfo info = MnemonicInfo.extract(value);
			// Set the property with the resource value
			label.setText(info.getText());
			label.setDisplayedMnemonic(info.getMnemonic());
			label.setDisplayedMnemonicIndex(info.getMnemonicIndex());
			return true;
		}
		return false;
	}
}
