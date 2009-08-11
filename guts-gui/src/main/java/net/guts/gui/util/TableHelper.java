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

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Resource;

import com.google.inject.Inject;

/**
 * Helper class used to inject column names into the column headers of a 
 * {@link JTable}. Such a class is needed because Swing Application Framework
 * does not support column header labels injection out of the box.
 * 
 * @author Jean-Francois Poilpret
 */
final public class TableHelper
{
	private TableHelper()
	{
	}
	
	/**
	 * Injects labels into column headers of a table. {@code table} must have a 
	 * unique name (set by {@link JTable#setName(String)}) and the application
	 * properties file must include properties for each column labels as follows:
	 * <pre>
	 * name.column[0]=First Name
	 * name.column[1]=Last Name
	 * name.column[2]=Home City
	 * </pre>
	 * Where {@code name} must be replaced by the name of the table. There must
	 * be exactly as many columns defined in the properties file as in the table.
	 * 
	 * @param table the table which column headers labels must be injected 
	 */
	static public void injectColumnNames(JTable table)
	{
		TableColumnModel model = table.getColumnModel();
		ColumnNames names = new ColumnNames(model.getColumnCount());
		//#### Needs AppFW Issue #11 fixed
		_context.getResourceMap().injectFields(names, table.getName());
		for (int i = 0; i < model.getColumnCount(); i++)
		{
			if (names.getName(i) != null)
			{
				model.getColumn(i).setHeaderValue(names.getName(i));
			}
		}
	}

	static private class ColumnNames
	{
		public ColumnNames(int count)
		{
			_names = new String[count];
		}
		
		public String getName(int index)
		{
			return _names[index];
		}
		
		@Resource(key = "column") final private String[] _names;
	}
	
	/**
	 * Initialization method statically injected by Guice.
	 * 
	 * @param context the Swing Application Framework {@code ApplicationContext}
	 */
	@Inject static void setContext(ApplicationContext context)
	{
		_context = context;
	}
	
	static private ApplicationContext _context;
}
