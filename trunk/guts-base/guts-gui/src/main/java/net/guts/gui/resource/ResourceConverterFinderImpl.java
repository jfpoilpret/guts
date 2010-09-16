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

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

@Singleton class ResourceConverterFinderImpl implements ResourceConverterFinder
{
	@Inject ResourceConverterFinderImpl(
		Map<TypeLiteral<?>, ResourceConverter<?>> converters)
	{
		_converters = converters;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.resource.ResourceConverterFinder#getConverter(com.google.inject.TypeLiteral)
	 */
	@SuppressWarnings("unchecked") 
	public <T> ResourceConverter<T> getConverter(TypeLiteral<T> type)
	{
		ResourceConverter<T> converter = (ResourceConverter<T>) _converters.get(type);
		return converter;
	}

	final private Map<TypeLiteral<?>, ResourceConverter<?>> _converters;
}
