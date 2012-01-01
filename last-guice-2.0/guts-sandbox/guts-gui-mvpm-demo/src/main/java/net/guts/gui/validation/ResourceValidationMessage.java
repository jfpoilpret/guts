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

import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationMessage;
import com.jgoodies.validation.util.ValidationUtils;

class ResourceValidationMessage implements ValidationMessage
{
	@Inject ResourceValidationMessage(
		ResourceInjector injector,
		@Assisted String key, @Assisted Severity severity)
	{
		_injector = injector;
		_key = key;
		_severity = severity;
	}
	
	@Override public Severity severity()
	{
		return _severity;
	}
	
	@Override public String formattedText()
	{
		_injector.injectInstance(this, _key);
		return _text;
	}
	
	@Override public Object key()
	{
		return _key;
	}

	// This method is called by ResourceInjector
	void setText(String text)
	{
		_text = (text != null ? text : "");
	}

	@Override public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof ResourceValidationMessage))
		{
			return false;
		}
		ResourceValidationMessage other = (ResourceValidationMessage) o;
		return		_severity.equals(other._severity)
				&&	ValidationUtils.equals(_key, other._key)
				&&	ValidationUtils.equals(_text, other._text);
	}

    @Override public int hashCode()
	{
		int result = 17;
		result = 37 * result + _severity.hashCode();
		result = 37 * result + _key.hashCode();
		result = 37 * result + _text.hashCode();
		return result;
	}
	
	final private ResourceInjector _injector;
	final private String _key;
	final private Severity _severity;
	private String _text = "";
}
