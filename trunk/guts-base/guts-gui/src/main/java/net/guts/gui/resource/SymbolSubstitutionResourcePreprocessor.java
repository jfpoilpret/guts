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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.inject.Singleton;

@Singleton
class SymbolSubstitutionResourcePreprocessor implements ResourcePreprocessor
{
	@Override public String convert(ResourceMap map, String source)
	{
		Matcher matcher = _regexp.matcher(source);
		if (!matcher.find())
		{
			return source;
		}
		StringBuffer target = new StringBuffer();
		while (true)
		{
			// Get and substitute current symbol
			String symbol = substitute(matcher.group(1), map);
			matcher.appendReplacement(target, (symbol != null ? symbol : "$0"));
			if (!matcher.find())
			{
				matcher.appendTail(target);
				return target.toString();
			}
		}
	}
	
	protected String substitute(String symbol, ResourceMap map)
	{
		// Try to convert it to a matching value
		ResourceMap.Key key = map.getKey(symbol);
		if (key != null)
		{
			return map.getValue(key, String.class);
		}
		else
		{
			return null;
		}
	}

	// Note: real regexp is "\$\{([^\}]+)\}"
	static final private String SYMBOL_REGEXP = "\\$\\{([^\\}]+)\\}";
	static final private Pattern _regexp = Pattern.compile(SYMBOL_REGEXP);
}
