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

import javax.swing.JFileChooser;

import net.guts.common.bean.UntypedProperty;

class JFileChooserInjector extends AbstractComponentInjector<JFileChooser>
{
	@Override public void inject(JFileChooser chooser, ResourceMap resources)
	{
		String prefix = prefix(chooser);
		if (prefix == null)
		{
			return;
		}
		Class<? extends Component> componentType = chooser.getClass();
		// For each injectable resource
		for (ResourceMap.Key key: resources.keys(prefix))
		{
			String name = key.key();
			// Check if key is a special JTabbedPane property
			if (!handleChooserProperty(chooser, key, resources))
			{
				// Check that this property exists
				UntypedProperty property = writableProperty(name, componentType);
				if (property != null)
				{
					Class<?> type = property.type();
					// Get the value in the correct type
					Object value = resources.getValue(key, type);
					// Set the property with the resource value
					property.set(chooser, value);
				}
			}
		}
	}
	
	private boolean handleChooserProperty(
		JFileChooser chooser, ResourceMap.Key key, ResourceMap resources)
	{
		if (APPROVE_TAG.equals(key.key()))
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
