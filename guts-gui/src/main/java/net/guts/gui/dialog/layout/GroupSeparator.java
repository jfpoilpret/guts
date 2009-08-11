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

package net.guts.gui.dialog.layout;

import javax.swing.JLabel;
import javax.swing.JSeparator;

import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * Helper class that adds a "Group" separator in a dialog panel that uses
 * {@link DesignGridLayout}. This follows Karsten Lentzsch guidelines in 
 * JGoodies.
 * <p/>
 * Note: this will not work with a panel that uses any other 
 * {@link java.awt.LayoutManager} than {@link DesignGridLayout}.
 * 
 * @author Jean-Francois Poilpret
 */
final public class GroupSeparator
{
	private GroupSeparator()
	{
	}
	
	/**
	 * Adds a new separtor group to the panel managed by the given {@code layout}.
	 * 
	 * @param name name of the group; this will be used to name each component
	 * ({@link JLabel} and {@link JSeparator} added to the panel, so that resource
	 * injection can work on them.
	 * @param layout the layout to add the group to
	 */
	static public void add(String name, DesignGridLayout layout)
	{
		JLabel lblGroup = new JLabel();
		lblGroup.setName(name + "-label");
		lblGroup.setHorizontalAlignment(JLabel.LEADING);
		layout.emptyRow();
		layout.row().left().fill().add(lblGroup, new JSeparator());
	}
}
