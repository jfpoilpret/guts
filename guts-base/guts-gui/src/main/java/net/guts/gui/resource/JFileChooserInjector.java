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

import javax.swing.JFileChooser;

import net.guts.gui.resource.ResourceMap.Key;

/**
 * Features of this injector are documented in {@link ResourceModule}.
 */
class JFileChooserInjector extends BeanPropertiesInjector<JFileChooser>
{
	@Override protected boolean handleSpecialProperty(
		JFileChooser chooser, Key key, ResourceMap resources)
	{
		if (APPROVE_TAG.equals(key.name()))
		{
			String text = resources.getValue(key, String.class);
			MnemonicInfo info = MnemonicInfo.extract(text);
			chooser.setApproveButtonText(info.getText());
			chooser.setApproveButtonMnemonic(info.getMnemonic());
			return true;
		}
		return false;
	}
	
	static final private String APPROVE_TAG = "approveButtonText";
}
