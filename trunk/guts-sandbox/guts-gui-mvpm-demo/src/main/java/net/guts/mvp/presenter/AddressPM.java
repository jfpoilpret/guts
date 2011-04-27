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

package net.guts.mvp.presenter;

import net.guts.binding.GutsPresentationModel;
import net.guts.binding.Models;
import net.guts.mvp.domain.Address;

import com.jgoodies.binding.value.AbstractConverter;
import com.jgoodies.binding.value.ValueModel;

public class AddressPM
{
	AddressPM(ValueModel<Address> addressModel)
	{
		// Create all necessary models here
		address = Models.createPM(Address.class, addressModel);

		Address of = address.of();
		street1 = address.getPropertyModel(of.getStreet1());
		street2 = address.getPropertyModel(of.getStreet2());
		zip = address.getPropertyModel(of.getZip());
		city = address.getPropertyModel(of.getCity());
		phone = address.getPropertyModel(of.getPhone());
		
		compactAddress = new CompactAddressConverter(addressModel); 
	}
	
	@SuppressWarnings("serial") 
	static private class CompactAddressConverter extends AbstractConverter<Address, String>
	{
		CompactAddressConverter(ValueModel<Address> address)
		{
			super(address);
		}
		
		@Override public void setValue(String compactAddress)
		{
			throw new UnsupportedOperationException(
				"Can't convert address from compact format String to an Address value");
		}

		@Override public String convertFromSubject(Address address)
		{
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
	}

	//TODO does it need be public????
	final public GutsPresentationModel<Address> address;
	
	final public ValueModel<String> street1;
	final public ValueModel<String> street2;
	final public ValueModel<String> zip;
	final public ValueModel<String> city;
	final public ValueModel<String> phone;
	final public ValueModel<String> compactAddress;
}
