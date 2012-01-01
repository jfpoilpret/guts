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

package net.guts.gui.validation;

import com.google.inject.Inject;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.ValidationResult;

public final class ValidationHelper
{	
	private ValidationHelper()
	{
	}
	
	static public void checkMandatory(
		ValueModel<String> model, ValidationResult result, String key)
	{
		if (isEmpty(model))
		{
			result.add(createMessage(key, Severity.ERROR));
		}
	}
	
	static public boolean isEmpty(ValueModel<String> model)
	{
		String value = model.getValue();
		return value == null || value.trim().isEmpty();
	}
	
	static public ValidationMessage createMessage(String key, Severity severity)
	{
		return _messageFactory.create(key, severity);
	}
	
	@Inject static private ValidationMessageFactory _messageFactory;
}
