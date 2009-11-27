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

import java.util.ArrayList;
import java.util.List;

import net.guts.common.type.TypeHelper;

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
		bundle = Bundle.checkBundleExists(bundle, null);
		if (bundle != null)
		{
			binder.bind(String.class).annotatedWith(BindBundle.class).toInstance(bundle);
		}
	}
	
	/**
	 * Defines the "root bundle" location where {@link ResourceInjector} first search
	 * for properties during resources injection.
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}:
	 * <pre>
	 * Resources.bindRootBundle(binder(), this.getClass(), "bundlename");
	 * </pre>
	 * <p/>
	 * Defining a root bundle is optional but strongly advised. It is not supported
	 * to define more than one root bundle (that would generate an error at Guice 
	 * {@link com.google.inject.Injector} creation).
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param reference the class which package location serves as a reference to
	 * determine the relative bundle location in {@code root}
	 * @param root the path to bundle file, not including the ".properties" extension; 
	 * this file must exist on the classpath.
	 */
	static public void bindRootBundle(Binder binder, Class<?> reference, String root)
	{
		String bundle = Bundle.checkBundleExists(root, reference);
		if (bundle != null)
		{
			binder.bind(String.class).annotatedWith(BindBundle.class).toInstance(bundle);
		}
	}
	
	/**
	 * Binds a list of resource bundles (defined by paths) to a given class.
	 * This call is fully equivalent to {@link UsesBundles} annotation applied
	 * on the given class.
	 * <p/>
	 * This can be useful when reusing 3rd-party components which source code you 
	 * can't modify, but still want to be able to inject them with a default set
	 * of resource bundles.
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type the class to which you want to bind {@code bundles}
	 * @param bundles the list of resource bundles paths to be bound to {@code type};
	 * each path can be absolute or relative (as explained in {@link UsesBundles}.
	 * 
	 * @see UsesBundles
	 */
	static public void bindClassBundles(Binder binder, Class<?> type, String... bundles)
	{
		// First check each bundle exists
		List<String> acceptedBundles = acceptedBundles(type, bundles);
		// Then add the list of Bundles to type (MapBinder)
		if (!acceptedBundles.isEmpty())
		{
			classBundlesMap(binder).addBinding(type).toInstance(acceptedBundles);
		}
	}
	
	/**
	 * Binds a list of resource bundles (defined by paths) to a given package.
	 * This call is fully equivalent to {@link UsesBundles} annotation applied
	 * on the given package (in {@code package-info.java}).
	 * <p/>
	 * This can be useful when reusing 3rd-party components which source code you 
	 * can't modify, but still want to be able to inject them with a default set
	 * of resource bundles.
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type the class defining the package to which you want to bind {@code bundles}
	 * @param bundles the list of resource bundles paths to be bound to the package 
	 * of {@code type}; each path can be absolute or relative (as explained in 
	 * {@link UsesBundles}.
	 * 
	 * @see UsesBundles
	 */
	static public void bindPackageBundles(Binder binder, Class<?> type, String... bundles)
	{
		// First check each bundle exists
		List<String> acceptedBundles = acceptedBundles(type, bundles);
		// Then add the list of Bundles to type (MapBinder)
		if (!acceptedBundles.isEmpty())
		{
			String pack = TypeHelper.getPackage(type);
			packageBundlesMap(binder).addBinding(pack).toInstance(acceptedBundles);
		}
	}
	
	static private List<String> acceptedBundles(Class<?> type, String... bundles)
	{
		List<String> acceptedBundles = new ArrayList<String>();
		// First check each bundle exists
		for (String bundle: bundles)
		{
			String fullPathBundle = Bundle.checkBundleExists(bundle, type);
			if (fullPathBundle != null)
			{
				acceptedBundles.add(fullPathBundle);
			}
		}
		return acceptedBundles;
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
	 * Creates and binds a special {@link ResourceConverter} for a given {@code enum} 
	 * type.
	 * <p/>
	 * This allows you to define properties in resource bundles which value can be one of
	 * the enum values of a given enum type. During injection the enum value string is
	 * converted to the real enum instance.
	 * 
	 * @param <T> enum type for which to create and bind a {@code ResourceConverter}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type enum type for which to create and bind a {@code ResourceConverter}
	 */
	static public <T extends Enum<T>> void bindEnumConverter(Binder binder, Class<T> type)
	{
		bindConverter(binder, type).toInstance(new EnumConverter<T>(type));
	}

	/**
	 * Creates and binds a special {@link ResourceConverter} for a given 
	 * {@code Class<? extends T>} type.
	 * <p/>
	 * This allows you to define properties in resource bundles which value is the fully
	 * qualified name of a java class.
	 * <p/>
	 * The snippets below show a typical usage example; first in one of your 
	 * {@link com.google.inject.Module}s:
	 * <pre>
	 * Resources.bindClassConverter(binder(), javax.swing.LookAndFeel.class);
	 * </pre>
	 * Then, in a component that will have its resources injected by Guts-GUI:
	 * <pre>
	 * public MyComponent
	 * {
	 *     ...
	 *     public void setLookAndFeel(Class&lt;? extends LookAndFeel&gt; laf)
	 *     {
	 *         ...
	 *     }
	 * }
	 * </pre>
	 * This property would be set in a resource bundle this way:
	 * <pre>
	 * MyComponent.lookAndFeel = org.jvnet.substance.skin.SubstanceBusinessLookAndFeel
	 * </pre>
	 * 
	 * @param <T> type for which to create and bind a {@code ResourceConverter}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type type for which to create and bind a {@code ResourceConverter}
	 */
	@SuppressWarnings("unchecked") 
	static public <T> void bindClassConverter(Binder binder, Class<T> type)
	{
		TypeLiteral<Class<? extends T>> literal = (TypeLiteral<Class<? extends T>>) 
			TypeLiteral.get(Types.newParameterizedType(Class.class, Types.subtypeOf(type)));
		bindConverter(binder, literal).toInstance(new ClassConverter<T>(type));
	}

	/**
	 * Creates and binds a special {@link ResourceConverter} for a given 
	 * {@code List<T>} type.
	 * <p/>
	 * This allows you to define properties in resource bundles which value is made of
	 * a list of ":" separated values, each of which will be converted to type {@code T}
	 * and added to a {@link List}.
	 * <p/>
	 * The snippets below show a typical usage example; first in one of your 
	 * {@link com.google.inject.Module}s:
	 * <pre>
	 * Resources.bindListConverter(binder(), String.class);
	 * </pre>
	 * Then, in a component that will have its resources injected by Guts-GUI:
	 * <pre>
	 * public MyComponent
	 * {
	 *     ...
	 *     public void setNames(List&lt;String&gt; names)
	 *     {
	 *         ...
	 *     }
	 * }
	 * </pre>
	 * This property would be set in a resource bundle this way:
	 * <pre>
	 * MyComponent.names = Name1:Name2:Name3
	 * </pre>
	 * 
	 * @param <T> type of the items in the {@link List} for which to create and 
	 * bind a {@code ResourceConverter}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type type of the items in the {@link List} for which to create and 
	 * @param delimiters list of delimiters characters that should be used to extract
	 * each item of the list in the original resource string value
	 */
	static public <T> void bindListConverter(
		Binder binder, Class<T> type, String delimiters)
	{
		bindListConverter(binder, TypeLiteral.get(type), delimiters);
	}

	/**
	 * Creates and binds a special {@link ResourceConverter} for a given 
	 * {@code List<T>} type.
	 * 
	 * @param <T> type of the items in the {@link List} for which to create and 
	 * bind a {@code ResourceConverter}
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @param type type of the items in the {@link List} for which to create and 
	 * @param delimiters list of delimiters characters that should be used to extract
	 * each item of the list in the original resource string value
	 * 
	 * @see #bindListConverter(Binder, Class)
	 */
	@SuppressWarnings("unchecked") 
	static public <T> void bindListConverter(
		Binder binder, TypeLiteral<T> type, String delimiters)
	{
		TypeLiteral<List<T>> list = (TypeLiteral<List<T>>)
			TypeLiteral.get(Types.listOf(type.getType()));
		bindConverter(binder, list).toInstance(new ListConverter<T>(type, delimiters));
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
	 * Overrides the default {@link InjectionDecisionStrategy} used by 
	 * {@link ResourceInjector}.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Resources.bindInjectionStrategy(binder()).to(MyStrategy.class);
	 * </pre>
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * <p/>
	 * Note that you normally won't need to override Guts-GUI default strategy that,
	 * for Swing components, checks if the component was already injected for the
	 * given {@code Locale}, and forces injection only if needed.
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind 
	 * to an {@link InjectionDecisionStrategy}
	 */
	static public LinkedBindingBuilder<InjectionDecisionStrategy> bindInjectionStrategy(
		Binder binder)
	{
		return binder.bind(InjectionDecisionStrategy.class);
	}

	/**
	 * Overrides the default {@link ResourcePreprocessor} used by 
	 * {@link ResourceInjector}.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Resources.bindResourcePreprocessor(binder()).to(MyPreprocessor.class);
	 * </pre>
	 * <p/>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind 
	 * to a {@link ResourceInjector}
	 */
	static public LinkedBindingBuilder<ResourcePreprocessor> bindResourcePreprocessor(
		Binder binder)
	{
		return binder.bind(ResourcePreprocessor.class);
	}

	static MapBinder<Class<?>, List<String>> classBundlesMap(Binder binder)
	{
		return MapBinder.newMapBinder(binder, CLASS_TYPE, LIST_STRING_TYPE, BindBundle.class);
	}

	static MapBinder<String, List<String>> packageBundlesMap(Binder binder)
	{
		return MapBinder.newMapBinder(binder, STRING_TYPE, LIST_STRING_TYPE, BindBundle.class);
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
	static private final TypeLiteral<String> STRING_TYPE = TypeLiteral.get(String.class);
	static private final TypeLiteral<List<String>> LIST_STRING_TYPE =
		new TypeLiteral<List<String>>() {};
	static private final TypeLiteral<ResourceConverter<?>> RESOURCE_CONVERTER_TYPE =
		new TypeLiteral<ResourceConverter<?>>() {};
	static private final TypeLiteral<InstanceInjector<?>> COMPONENT_INJECTOR_TYPE = 
		new TypeLiteral<InstanceInjector<?>>() {};
}
