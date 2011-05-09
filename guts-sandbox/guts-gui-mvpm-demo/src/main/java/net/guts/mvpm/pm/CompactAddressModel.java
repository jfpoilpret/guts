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

package net.guts.mvpm.pm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.guts.binding.GutsPresentationModel;
import net.guts.binding.Models;
import net.guts.mvpm.domain.Address;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
class CompactAddressModel extends AbstractValueModel<String>
{
	CompactAddressModel(ValueModel<Address> model)
	{
		this.model = model;
		model.addValueChangeListener(listener);
		GutsPresentationModel<Address> addressModel = Models.createPM(Address.class, model);
		Address of = addressModel.of();
		addressModel.getPropertyModel(of.getStreet1()).addValueChangeListener(listener);
		addressModel.getPropertyModel(of.getStreet2()).addValueChangeListener(listener);
		addressModel.getPropertyModel(of.getZip()).addValueChangeListener(listener);
		addressModel.getPropertyModel(of.getCity()).addValueChangeListener(listener);
	}
	
	@Override public void setValue(String compactAddress)
	{
		throw new UnsupportedOperationException(
			"Can't convert address from compact format String to an Address value");
	}

	@Override public String getValue()
	{
		Address address = model.getValue();
		if (address != null)
		{
			StringBuilder builder = new StringBuilder();
			append(builder, address.getStreet1());
			append(builder, address.getStreet2());
			append(builder, address.getZip(), address.getCity());
			return builder.toString();
		}
		else
		{
			return null;
		}
	}
	
	static private void append(StringBuilder builder, String part1, String part2)
	{
		String line;
		if (part1 != null && part1.trim().length() != 0)
		{
			line = part1 + " " + part2;
		}
		else
		{
			line = part2;
		}
		append(builder, line);
	}
	
	static private void append(StringBuilder builder, String line)
	{
		if (line != null && line.trim().length() != 0)
		{
			if (builder.length() > 0)
			{
				builder.append('\n');
			}
			builder.append(line.trim());
		}
	}

	final private ValueModel<Address> model;
	final private PropertyChangeListener listener = new PropertyChangeListener()
	{
		@Override public void propertyChange(PropertyChangeEvent evt)
		{
			fireValueChange(null, getValue(), true);
		}
	};
}
