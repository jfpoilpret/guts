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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

class ConverterProvider<T> implements Provider<ResourceConverter<T>>
{
	ConverterProvider(TypeLiteral<T> type)
	{
		_type = type;
	}
	
	/* (non-Javadoc)
	 * @see com.google.inject.Provider#get()
	 */
	@Override public ResourceConverter<T> get()
	{
		return _finder.getConverter(_type);
	}

	@Inject void setFinder(ResourceConverterFinder finder)
	{
		_finder = finder;
	}
	
	final private TypeLiteral<T> _type;
	private ResourceConverterFinder _finder;
}
