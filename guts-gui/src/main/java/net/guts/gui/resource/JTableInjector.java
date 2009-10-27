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

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.guts.gui.resource.ResourceMap.Key;

class JTableInjector extends BeanPropertiesInjector<JTable>
{
	@Override protected boolean handleSpecialProperty(
		JTable table, Key key, ResourceMap resources)
	{
		// Prepare injection of column header text
		TableColumnModel model = table.getColumnModel();
		String name = key.key();
		// Check if key is a special JTable property
		TableColumn column = column(name, model);
		if (column != null)
		{
			// Yes, inject directly into JTable column
			String value = resources.getValue(key, String.class);
			column.setHeaderValue(value);
			return true;
		}
		return false;
	}

	private TableColumn column(String name, TableColumnModel model)
	{
		if (!name.startsWith(HEADER_TAG))
		{
			return null;
		}
		try
		{
			String suffix = name.substring(HEADER_TAG_LEN);
			int index = Integer.parseInt(suffix);
			if (index < 0 || index >= model.getColumnCount())
			{
				//TODO log something
				return null;
			}
			else
			{
				return model.getColumn(index);
			}
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	static final private String HEADER_TAG = "header";
	static final private int HEADER_TAG_LEN = HEADER_TAG.length();
}
