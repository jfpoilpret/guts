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

package net.guts.binding;

import java.util.ArrayList;
import java.util.List;

import net.guts.properties.Property;

import com.jgoodies.binding.adapter.AbstractTableAdapter;

public class GutsTableModel<E> extends AbstractTableAdapter<E>
{
	GutsTableModel(List<Property<E, ?>> properties)
	{
		super(propertyNames(properties));
		_properties = properties;
	}

	@Override public Class<?> getColumnClass(int columnIndex)
	{
		return _properties.get(columnIndex).type().getRawType();
	}

	@Override public Object getValueAt(int rowIndex, int columnIndex)
	{
		E item = getRow(rowIndex);
		return _properties.get(columnIndex).get(item);
	}
	
	static private <E> String[] propertyNames(List<Property<E, ?>> properties)
	{
		List<String> names = new ArrayList<String>(properties.size());
		for (Property<?, ?> property: properties)
		{
			names.add(property.name());
		}
		return names.toArray(new String[0]);
	}
	
	final private List<Property<E, ?>> _properties;
}
