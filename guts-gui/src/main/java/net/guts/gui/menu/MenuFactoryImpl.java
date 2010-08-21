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

package net.guts.gui.menu;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.guts.gui.action.GutsAction;

import com.google.inject.Singleton;

@Singleton
class MenuFactoryImpl implements MenuFactory
{
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.menu.MenuFactory#createMenu(java.lang.String, java.lang.String[])
	 */
	@Override public JMenu createMenu(String name, GutsAction... actions)
	{
		JMenu menu = new JMenu();
		initMenu(menu, name, actions);
		return menu;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.menu.MenuFactory#createPopupMenu(java.lang.String, java.lang.String[])
	 */
	public JPopupMenu createPopupMenu(String name, GutsAction... actions)
	{
		JPopupMenu popup = new JPopupMenu();
		initMenu(popup, name, actions);
		return popup;
	}
	
	private <T extends JComponent> void initMenu(T menu, String name, GutsAction[] actions)
	{
		menu.setName(name);
		for (GutsAction action: actions)
		{
			if (action == ACTION_SEPARATOR)
			{
				menu.add(new JSeparator());
			}
			else
			{
				JMenuItem item = new JMenuItem();
				item.setName(menu.getName() + "-" + action.name());
				item.setAction(action);
				menu.add(item);
			}
		}
	}
}
