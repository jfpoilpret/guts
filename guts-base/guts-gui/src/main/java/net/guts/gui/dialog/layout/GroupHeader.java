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

import net.guts.gui.naming.ComponentHolder;
import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * Helper class that adds a "Group" header (separator) in a dialog panel 
 * that uses {@link DesignGridLayout}. This follows Karsten Lentzsch 
 * guidelines in JGoodies.
 * <p/>
 * Note: this will not work with a panel that uses any other 
 * {@link java.awt.LayoutManager} than {@link DesignGridLayout}.
 * 
 * @author Jean-Francois Poilpret
 */
public final class GroupHeader implements ComponentHolder
{
	/**
	 * Create a new group header, with no name. This factory method should be
	 * used along with guts-gui automatic naming of components.
	 */
	static public GroupHeader create()
	{
		return new GroupHeader();
	}

	/**
	 * Create a new group header, with the given {@code name}. 
	 */
	static public GroupHeader create(String name)
	{
		GroupHeader header = create();
		header.setName(name);
		return header;
	}

	private void setName(String name)
	{
		if (name != null && _name.getName() == null)
		{
			_name.setName(name);
		}
	}

	/**
	 * This method must be called on {@code this} group header to add it to
	 * a panel. This method must be called only once for {@code this} header.
	 */
	public void layout(DesignGridLayout layout)
	{
		_name.setHorizontalAlignment(JLabel.LEADING);
		layout.emptyRow();
		layout.row().left().fill().add(_name, _ruler);
	}
	
	private GroupHeader()
	{
	}
	
	final private JLabel _name = new JLabel();
	final private JSeparator _ruler = new JSeparator();
}
