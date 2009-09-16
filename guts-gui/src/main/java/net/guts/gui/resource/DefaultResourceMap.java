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

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

// This implements search of resources across all bundles
@Singleton
class DefaultResourceMap implements ResourceMap
{
	// Should we use ResourceBundle here? Not so sure!
	@Inject DefaultResourceMap(List<ResourceBundle> bundles, 
		Map<TypeLiteral<?>, ResourceConverter<?>> converters)
	{
		_bundles = bundles;
		_converters = converters;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.resource.spi.ResourceMap#getValue(java.lang.String, java.lang.Class)
	 */
	@Override public <T> T getValue(String prefix, String key, Class<T> type)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.resource.spi.ResourceMap#keys()
	 */
	@Override public Set<String> keys(String prefix)
	{
		// TODO Auto-generated method stub
		return null;
	}

	final private List<ResourceBundle> _bundles;
	final private Map<TypeLiteral<?>, ResourceConverter<?>> _converters;
}
