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
	
	@SuppressWarnings("unchecked")
	static public <T> LinkedBindingBuilder<ComponentInjector<T>> bindComponentInjector(
		Binder binder, Class<T> type)
	{
		LinkedBindingBuilder builder = injectors(binder).addBinding(type);
		return builder;
	}
	
	static public void bindRootBundle(Binder binder, Class<?> root)
	{
		String bundle = bundlePath(root);
		binder.bind(String.class).annotatedWith(RootBundle.class).toInstance(bundle);
	}

	static String bundlePath(Class<?> name)
	{
		String bundle = name.getCanonicalName();
		if (bundle == null)
		{
			return null;
		}
		bundle = bundle.substring(0, bundle.lastIndexOf('.'));
		return bundle;
	}

	static private MapBinder<TypeLiteral<?>, ResourceConverter<?>> converters(Binder binder)
	{
		return MapBinder.newMapBinder(binder, TYPE_LITERAL_TYPE, RESOURCE_CONVERTER_TYPE);
	}
	
	static private MapBinder<Class<?>, ComponentInjector<?>> injectors(Binder binder)
	{
		return MapBinder.newMapBinder(binder, CLASS_TYPE, COMPONENT_INJECTOR_TYPE);
	}
	
	static private final TypeLiteral<TypeLiteral<?>> TYPE_LITERAL_TYPE =
		new TypeLiteral<TypeLiteral<?>>() {};
	static private final TypeLiteral<Class<?>> CLASS_TYPE =
		new TypeLiteral<Class<?>>() {};
	static private final TypeLiteral<ResourceConverter<?>> RESOURCE_CONVERTER_TYPE =
		new TypeLiteral<ResourceConverter<?>>() {};
	static private final TypeLiteral<ComponentInjector<?>> COMPONENT_INJECTOR_TYPE = 
		new TypeLiteral<ComponentInjector<?>>() {};
}
