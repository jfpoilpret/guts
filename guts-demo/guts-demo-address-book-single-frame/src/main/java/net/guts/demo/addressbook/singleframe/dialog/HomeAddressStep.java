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

package net.guts.demo.addressbook.singleframe.dialog;

import net.guts.demo.addressbook.singleframe.domain.Address;
import net.guts.demo.addressbook.singleframe.domain.Contact;

public class HomeAddressStep extends AbstractAddressStep
{
	static final private long serialVersionUID = -5631169946204437002L;

	@Override protected Address getAddress(Contact contact)
    {
	    return contact.getHome();
    }
}