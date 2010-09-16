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

package net.guts.gui.examples.addressbook.domain;

import java.util.Date;

public class Contact
{
	private int _id;
	private String _firstName;
	private String _lastName;
	private Date _birth;
	private Address _home = new Address();
	private Address _office = new Address();

	public int getId()
	{
		return _id;
	}
	public void setId(int id)
	{
		this._id = id;
	}
	public Date getBirth()
	{
		return _birth;
	}
	public void setBirth(Date birth)
	{
		this._birth = birth;
	}
	public String getFirstName()
	{
		return _firstName;
	}
	public void setFirstName(String firstName)
	{
		this._firstName = firstName;
	}
	public Address getHome()
	{
		return _home;
	}
	public void setHome(Address home)
	{
		this._home = home;
	}
	public String getLastName()
	{
		return _lastName;
	}
	public void setLastName(String lastName)
	{
		this._lastName = lastName;
	}
	public Address getOffice()
	{
		return _office;
	}
	public void setOffice(Address office)
	{
		this._office = office;
	}
	
	public Contact copy()
	{
		Contact clone = new Contact();
		clone._id = this._id;
		clone._firstName = this._firstName;
		clone._lastName = this._lastName;
		clone._birth = this._birth;
		clone._home = this._home.copy();
		clone._office = this._office.copy();
		return clone;
	}
}
