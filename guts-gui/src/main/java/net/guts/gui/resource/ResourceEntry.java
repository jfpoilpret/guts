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

package net.guts.gui.resource;

import java.net.URL;

public final class ResourceEntry
{
	ResourceEntry(String value, String source)
	{
		_value = value;
		_source = source;
	}
	
	public ResourceEntry derive(String value)
	{
		return new ResourceEntry(value, _source);
	}
	
	public String value()
	{
		return _value;
	}
	
	public URL valueAsUrl()
	{
		// Build path to resource if not absolute
		String path;
		if (_value.startsWith("/"))
		{
			path = _value.substring(1);
		}
		else
		{
			path = _source.replaceAll("\\.", "\\/") + "/" + _value;
		}
		return Thread.currentThread().getContextClassLoader().getResource(path);
	}

	final private String _value;
	final private String _source;
}
