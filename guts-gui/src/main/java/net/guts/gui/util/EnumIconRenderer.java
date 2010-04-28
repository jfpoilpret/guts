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

package net.guts.gui.util;

import java.awt.Component;
import java.net.URL;
import java.util.EnumMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

//TODO add system for automatic resource injection!
public class EnumIconRenderer<T extends Enum<T>> extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 4287379175359009250L;

	public EnumIconRenderer(Class<T> type)
	{
		setHorizontalAlignment(CENTER);
		_icons = new EnumMap<T, Icon>(type);
	}
	
	public EnumIconRenderer<T> mapIcon(T value, Icon icon)
	{
		_icons.put(value, icon);
		return this;
	}

	//TODO remove later (when resource injection is handled); for tests only
	public EnumIconRenderer<T> mapIcon(T value, String icon)
	{
		URL url = Thread.currentThread().getContextClassLoader().getResource(icon);
		return mapIcon(value, new ImageIcon(url));
	}

	@Override public Component getTableCellRendererComponent(JTable table, Object value, 
		boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setText("");
		setIcon(_icons.get(value));
		return this;
	}

	final private EnumMap<T, Icon> _icons;
}
