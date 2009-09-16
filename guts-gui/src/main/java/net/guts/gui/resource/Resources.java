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
import com.google.inject.multibindings.Multibinder;

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
	
	@SuppressWarnings("unchecked")
	static public <T> LinkedBindingBuilder<ComponentInjector<T>> bindComponentInjector(
		Binder binder, Class<T> type)
	{
		LinkedBindingBuilder builder = injectors(binder).addBinding(type);
		return builder;
	}
	
	static public void registerBundle(Binder binder, Package module, Package... dependencies)
	{
		bundles(binder).addBinding().toInstance(
			new ModuleBundleDefinition(module, dependencies));
	}
	
	static private Multibinder<ModuleBundleDefinition> bundles(Binder binder)
	{
		return Multibinder.newSetBinder(binder, ModuleBundleDefinition.class);
	}
	
	static private MapBinder<TypeLiteral<?>, ResourceConverter<?>> converters(Binder binder)
	{
		return MapBinder.newMapBinder(binder, CONVERTER_KEY_TYPE, CONVERTER_VALUE_TYPE);
	}
	
	static private MapBinder<Class<?>, ComponentInjector<?>> injectors(Binder binder)
	{
		return MapBinder.newMapBinder(binder, INJECTOR_KEY_TYPE, INJECTOR_VALUE_TYPE);
	}
	
	static private final TypeLiteral<TypeLiteral<?>> CONVERTER_KEY_TYPE =
		new TypeLiteral<TypeLiteral<?>>() {};
	static private final TypeLiteral<ResourceConverter<?>> CONVERTER_VALUE_TYPE =
		new TypeLiteral<ResourceConverter<?>>() {};
	static private final TypeLiteral<Class<?>> INJECTOR_KEY_TYPE =
		new TypeLiteral<Class<?>>() {};
	static private final TypeLiteral<ComponentInjector<?>> INJECTOR_VALUE_TYPE = 
		new TypeLiteral<ComponentInjector<?>>() {};
}
