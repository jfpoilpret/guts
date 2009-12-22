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

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import net.guts.gui.action.GutsAction;


import com.google.inject.ImplementedBy;

/**
 * Interface defining the factory to create menus and popup menus in the
 * application. All menus are defined by the names of matching {@code @Action}
 * annotated methods (all these actions must be known and retrievable by
 * {@link net.guts.gui.action.ActionManager#getAction(String)}).
 * <p/>
 * It is possible to define separators inside created menus by using a special
 * action name, {@link #ACTION_SEPARATOR}.
 * <p/>
 * This service is directly injectable through Guice facilities.
 * 
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(MenuFactoryImpl.class)
public interface MenuFactory
{
	/**
	 * Special action name, to use in arguments of MenuFactory methods, to
	 * describe a menu separator between two menu items.
	 */
	static final public GutsAction ACTION_SEPARATOR = null;

	/**
	 * Creates a new menu, to be added to a {@link javax.swing.JMenuBar}.
	 * The method creates a {@link javax.swing.JMenuItem} for every single
	 * action from the {@code actions} argument.
	 * <p/>
	 * All created widgets (except menu separators) are named according to the
	 * following conventions:
	 * <ul>
	 * <li>JMenu: {@code name}</li>
	 * <li>JMenuItem: {@code name-action}</li>
	 * </ul>
	 * 
	 * @param name name to assign to the created {@code JMenu}
	 * @param actions list of action names to map to {@code JMenuItem}s added to
	 * the menu; items are added in the order of the actions.
	 * @return a new menu, ready to be added to a {@link javax.swing.JMenuBar}
	 */
	public JMenu createMenu(String name, GutsAction... actions);
	
	/**
	 * Creates a new popup menu, that can be popped up on any component.
	 * The method creates a {@link javax.swing.JMenuItem} for every single
	 * action from the {@code actions} argument.
	 * <p/>
	 * All created widgets (except menu separators) are named according to the
	 * following conventions:
	 * <ul>
	 * <li>JPopupMenu: {@code name}</li>
	 * <li>JMenuItem: {@code name-action}</li>
	 * </ul>
	 * 
	 * @param name name to assign to the created {@code JPopupMenu}
	 * @param actions list of action names to map to {@code JMenuItem}s added to
	 * the popup menu; items are added in the order of the actions.
	 * @return a new popup menu
	 */
	public JPopupMenu createPopupMenu(String name, GutsAction... actions);
}
