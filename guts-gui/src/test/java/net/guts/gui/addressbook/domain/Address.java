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

package net.guts.gui.addressbook.domain;

public class Address
{
	private String _street1;
	private String _street2;
	private String _zip;
	private String _city;
	private String _country;
	
	private String _phone;

	public String getCity()
	{
		return _city;
	}

	public void setCity(String city)
	{
		_city = city;
	}

	public String getCountry()
	{
		return _country;
	}

	public void setCountry(String country)
	{
		_country = country;
	}

	public String getPhone()
	{
		return _phone;
	}

	public void setPhone(String phone)
	{
		_phone = phone;
	}

	public String getStreet1()
	{
		return _street1;
	}

	public void setStreet1(String street1)
	{
		_street1 = street1;
	}

	public String getStreet2()
	{
		return _street2;
	}

	public void setStreet2(String street2)
	{
		_street2 = street2;
	}

	public String getZip()
	{
		return _zip;
	}

	public void setZip(String zip)
	{
		_zip = zip;
	}
	
	public Address copy()
	{
		Address clone = new Address();
		clone._street1 = this._street1;
		clone._street2 = this._street2;
		clone._zip = this._zip;
		clone._city = this._city;
		clone._country = this._country;
		clone._phone = this._phone;
		return clone;
	}
}
