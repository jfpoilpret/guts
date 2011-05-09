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

import net.guts.binding.GutsPresentationModel;
import net.guts.binding.Models;
import net.guts.mvpm.domain.Address;

import com.jgoodies.binding.value.ValueModel;

public class AddressPM
{
	AddressPM(ValueModel<Address> addressModel)
	{
		// Create all necessary models here
		address = Models.createPM(Address.class, addressModel);

		Address of = address.of();
		street1 = address.getBufferedModel(of.getStreet1());
		street2 = address.getBufferedModel(of.getStreet2());
		zip = address.getBufferedModel(of.getZip());
		city = address.getBufferedModel(of.getCity());
		phone = address.getBufferedModel(of.getPhone());
	}
	
	final GutsPresentationModel<Address> address;
	
	final public ValueModel<String> street1;
	final public ValueModel<String> street2;
	final public ValueModel<String> zip;
	final public ValueModel<String> city;
	final public ValueModel<String> phone;
}
