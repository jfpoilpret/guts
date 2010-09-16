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

package net.guts.gui.action;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import net.guts.gui.resource.BeanPropertiesInjector;
import net.guts.gui.resource.MnemonicInfo;
import net.guts.gui.resource.ResourceMap;
import net.guts.gui.resource.ResourceMap.Key;

class GutsActionInjector extends BeanPropertiesInjector<GutsAction>
{
	@Override protected boolean handleSpecialProperty(GutsAction component, Key key,
		ResourceMap resources)
	{
		Action action = component.action();
		// First try to handle "easy" properties
		KeyHandler handler = new KeyHandler(action, resources, key);
		boolean ok = handler.handle("accelerator", Action.ACCELERATOR_KEY, KeyStroke.class);
		ok = ok || handler.handle("largeIcon", Action.LARGE_ICON_KEY, Icon.class);
		ok = ok || handler.handle("longDescription", Action.LONG_DESCRIPTION, String.class);
		ok = ok || handler.handle("toolTipText", Action.SHORT_DESCRIPTION, String.class);
		ok = ok || handler.handle("smallIcon", Action.SMALL_ICON, Icon.class);
		// Then deal with special "text" property (with mnemonics)
		if (!ok && key.name().equals("text"))
		{
			String text = resources.getValue(key, String.class);
			MnemonicInfo info = MnemonicInfo.extract(text);
			action.putValue(Action.NAME, info.getText());
			action.putValue(Action.MNEMONIC_KEY, info.getMnemonic());
			action.putValue(Action.DISPLAYED_MNEMONIC_INDEX_KEY, info.getMnemonicIndex());
			ok = true;
		}
		return ok;
	}

	static private class KeyHandler
	{
		KeyHandler(Action action, ResourceMap resources, Key key)
		{
			_action = action;
			_resources = resources;
			_key = key;
			_name = key.name();
		}
		
		boolean handle(String property, String actionKey, Class<?> type)
		{
			if (_name.equals(property))
			{
				_action.putValue(actionKey, _resources.getValue(_key, type));
				return true;
			}
			else
			{
				return false;
			}
		}
		
		final private Action _action;
		final private ResourceMap _resources;
		final private Key _key;
		final private String _name;
	}
}
