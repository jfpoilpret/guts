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

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.MapBinder;

/**
 * TODO
 *
 * @author Jean-Francois Poilpret
 */
public final class Resources
{
	private Resources()
	{
	}

	@SuppressWarnings("unchecked")
	static public <T> LinkedBindingBuilder<ResourceConverter<T>> bindConverter(
		Binder binder, TypeLiteral<T> type)
	{
		LinkedBindingBuilder builder = converters(binder).addBinding(type);
		return builder;
	}
	
	static public <T> LinkedBindingBuilder<ResourceConverter<T>> bindConverter(
		Binder binder, Class<T> type)
	{
		return bindConverter(binder, TypeLiteral.get(type));
	}
	
	static private MapBinder<TypeLiteral<?>, ResourceConverter<?>> converters(Binder binder)
	{
		return MapBinder.newMapBinder(binder, KEY_TYPE, VALUE_TYPE);
	}
	
	static private final TypeLiteral<TypeLiteral<?>> KEY_TYPE =
		new TypeLiteral<TypeLiteral<?>>() {};
	static private final TypeLiteral<ResourceConverter<?>> VALUE_TYPE =
		new TypeLiteral<ResourceConverter<?>>() {};
}
