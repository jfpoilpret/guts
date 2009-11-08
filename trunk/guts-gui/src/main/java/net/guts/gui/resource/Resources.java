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
import com.google.inject.util.Types;

/**
 * Utility class to define, from within a Guice {@link com.google.inject.Module},
 * bindings for {@link ResourceConverter}s, for {@link InstanceInjector}s, and
 * to declare a "root bundle" location where {@link ResourceInjector} will search 
 * for properties during resource injection.
 *
 * @author Jean-Francois Poilpret
 */
public final class Resources
{
	private Resources()
	{
	}

	/**
	 * Initializes a binding between a given property type and a matching 
	 * {@link ResourceConverter}.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Resources.bindConverter(binder(), new TypeLiteral&lt;List&lt;String&gt;&gt;)
	 *     .to(StringListConverter.class);
	 * </pre>
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 *  
	 * @param <T> property type to bind to a {@code ResourceConverter<T>}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type property type to bind to a {@code ResourceConverter<T>}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind 
	 * property {@code type} to an {@link ResourceConverter}
	 */
	@SuppressWarnings("unchecked")
	static public <T> LinkedBindingBuilder<ResourceConverter<T>> bindConverter(
		Binder binder, TypeLiteral<T> type)
	{
		// First bind a provider for ResourceConverter<T>
		TypeLiteral<ResourceConverter<T>> converter = converterType(type);
		if (converter != null)
		{
			binder.bind(converter).toProvider(new ConverterProvider<T>(type));
		}
		// Then start multibinding
		LinkedBindingBuilder builder = converters(binder).addBinding(type);
		return builder;
	}
	
	/**
	 * Initializes a binding between a given property type and a matching 
	 * {@link ResourceConverter}.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Resources.bindConverter(binder(), SomeType.class).to(SomeTypeConverter.class);
	 * </pre>
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 *  
	 * @param <T> property type to bind to a {@code ResourceConverter<T>}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type property type to bind to a {@code ResourceConverter<T>}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind 
	 * property {@code type} to an {@link ResourceConverter}
	 */
	static public <T> LinkedBindingBuilder<ResourceConverter<T>> bindConverter(
		Binder binder, Class<T> type)
	{
		return bindConverter(binder, TypeLiteral.get(type));
	}
	
	/**
	 * Initializes a binding between a given type of objects and a matching 
	 * {@link InstanceInjector}.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Resources.bindInstanceInjector(binder(), SomeType.class).to(SomeTypeInjector.class);
	 * </pre>
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 *  
	 * @param <T> class type to bind to a {@code InstanceInjector<T>}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type class type to bind to a {@code InstanceInjector<T>}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind 
	 * class {@code type} to an {@link InstanceInjector}
	 */
	@SuppressWarnings("unchecked")
	static public <T> LinkedBindingBuilder<InstanceInjector<T>> bindInstanceInjector(
		Binder binder, Class<T> type)
	{
		LinkedBindingBuilder builder = injectors(binder).addBinding(type);
		return builder;
	}

	/**
	 * Defines the "root bundle" location where {@link ResourceInjector} first search
	 * for properties during resources injection.
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}:
	 * <pre>
	 * Resources.bindRootBundle(binder(), "/somepath/bundlename");
	 * </pre>
	 * <p/>
	 * Defining a root bundle is optional but strongly advised. It is not supported
	 * to define more than one root bundle (that would generate an error at Guice 
	 * {@link com.google.inject.Injector} creation).
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param root the path to bundle file, not including the ".properties" extension; 
	 * this file must exist on the classpath.
	 */
	static public void bindRootBundle(Binder binder, String root)
	{
		String bundle = (root.startsWith("/") ? root : "/" + root);
		bundle = BundleHelper.checkBundleExists(bundle, null);
		if (bundle != null)
		{
			binder.bind(String.class).annotatedWith(RootBundle.class).toInstance(bundle);
		}
	}

	@SuppressWarnings("unchecked") 
	static private <T> TypeLiteral<ResourceConverter<T>> converterType(TypeLiteral<T> type)
	{
		if (type.getRawType().isPrimitive())
		{
			return null;
		}
		else
		{
			return (TypeLiteral<ResourceConverter<T>>) TypeLiteral.get(
				Types.newParameterizedType(ResourceConverter.class, type.getType()));
		}
	}

	static private MapBinder<TypeLiteral<?>, ResourceConverter<?>> converters(Binder binder)
	{
		return MapBinder.newMapBinder(binder, TYPE_LITERAL_TYPE, RESOURCE_CONVERTER_TYPE);
	}
	
	static private MapBinder<Class<?>, InstanceInjector<?>> injectors(Binder binder)
	{
		return MapBinder.newMapBinder(binder, CLASS_TYPE, COMPONENT_INJECTOR_TYPE);
	}
	
	static private final TypeLiteral<TypeLiteral<?>> TYPE_LITERAL_TYPE =
		new TypeLiteral<TypeLiteral<?>>() {};
	static private final TypeLiteral<Class<?>> CLASS_TYPE =
		new TypeLiteral<Class<?>>() {};
	static private final TypeLiteral<ResourceConverter<?>> RESOURCE_CONVERTER_TYPE =
		new TypeLiteral<ResourceConverter<?>>() {};
	static private final TypeLiteral<InstanceInjector<?>> COMPONENT_INJECTOR_TYPE = 
		new TypeLiteral<InstanceInjector<?>>() {};
}
