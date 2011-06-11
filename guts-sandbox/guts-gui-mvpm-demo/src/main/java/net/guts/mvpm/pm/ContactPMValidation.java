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

import static net.guts.gui.validation.ValidationHelper.*;

import com.jgoodies.validation.ValidationResult;

//TODO put back validation methods (not KEY constants?) into ContactPM?
public class ContactPMValidation
{	
	static final public String KEY_PREFIX_HOME = "HOME_ADDRESS_";
	static final public String KEY_PREFIX_OFFICE = "OFFICE_ADDRESS_";

	static final public String KEY_SUFFIX_MISSING_ZIP = "MISSING_ZIP";
	static final public String KEY_SUFFIX_MISSING_CITY = "MISSING_CITY";

	static final public String KEY_MANDATORY_FIRST_NAME = "MANDATORY_FIRST_NAME";
	static final public String KEY_MANDATORY_LAST_NAME = "MANDATORY_LAST_NAME";
	
	public ContactPMValidation(ContactPM model)
	{
		this.model = model;
	}
	
	public ValidationResult validate()
	{
		ValidationResult result = new ValidationResult();
		// Check mandatory fields first
		checkMandatory(model.firstName, result, KEY_MANDATORY_FIRST_NAME);
		checkMandatory(model.lastName, result, KEY_MANDATORY_LAST_NAME);
		// Check address is complete enough when some fields have been filled in
		checkAddress(model.homeAddress, result, KEY_PREFIX_HOME);
		checkAddress(model.officeAddress, result, KEY_PREFIX_OFFICE);
		return result;
	}

	static private void checkAddress(AddressPM address, ValidationResult result, String key)
	{
		if ((!isEmpty(address.street1)) || (!isEmpty(address.street2)))
		{
			checkMandatory(address.city, result, key + KEY_SUFFIX_MISSING_CITY);
			checkMandatory(address.zip, result, key + KEY_SUFFIX_MISSING_ZIP);
		}
		else if (isEmpty(address.city) != isEmpty(address.zip))
		{
			checkMandatory(address.city, result, key + KEY_SUFFIX_MISSING_CITY);
			checkMandatory(address.zip, result, key + KEY_SUFFIX_MISSING_ZIP);
		}
	}
	
	final private ContactPM model;
}
