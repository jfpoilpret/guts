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

import java.awt.Component;
import java.util.Locale;

import com.google.inject.ImplementedBy;

/**
 * Interface defining the API to inject resources into GUI components or, more
 * generally, into instances of any class.
 * <p/>
 * For GUI components injection, you normally won't need to directly use 
 * {@code ResourceInjector}, since {@link net.guts.gui.application.WindowController}
 * performs the necessary calls for you.
 * <p/>
 * Guts-GUI current implementation of {@code ResourceInjector} is looking for 
 * resources in resource bundles, defined as normal properties files accessible
 * from the classpath. Resource bundles are always named "resources.properties"
 * (or e.g. resources_en.properties for explicit english {@link java.util.Locale}).
 * <p/>
 * By using {@link UsesBundles} annotation, any class, or even a whole package (by
 * using the annotation in {@code package-info.java}), can specify one or several
 * determined resource bundle locations to be used when looking for resources to 
 * inject into it.
 * <p/>
 * In addition, you can specify, using {@link Resources#bindRootBundle}, a top
 * priority resource bundle, which content will always override, content from any
 * other bundles.
 * <p/>
 * When a given class has specified a list of resource bundles to be used for its
 * own resource injection, the order of priority of properties is: the root bundle
 * first, then (if a given resource was not found yet) in the first resource bundle
 * declared by the class, then the second, and so forth.
 * <p/>
 * In resource bundles, properties are always named according to the convention:
 * {@code objectname.propertyname}, where:
 * <ul>
 * <li>{@code objectname} is either the name of the component as returned by 
 * {@link java.awt.Component#getName()} or, for another type of object, the name
 * passed to {@link #injectInstance(Object, String)}</li>
 * <li>{@code propertyname} is the name of the java bean property to be injected 
 * into the component.</li>
 * </ul>
 * <p/>
 * {@code ResourceInjector} implementation is highly configurable, through the use 
 * of {@link Resources} utility methods. It uses standard properties files (as in 
 * {@link java.util.ResourceBundle}s) to look for properties to inject into objects.
 * <p/>
 * The way resources are injected into objects is indeed configurable in two axes:
 * <ul>
 * <li>Conversion of individual resources: {@link ResourceConverter} allows you to
 * define a way to convert the {@code String} value read from the properties file into
 * any specific type the injected instance needs.</li>
 * <li>Injection of instances of a given class: {@code ResourceInjector} uses
 * {@link InstanceInjector}s  to perform the actual work of injecting all necessary
 * properties into instances of a given class. By default, a general 
 * {@code InstanceInjector} subclass uses Java reflection to set properties from
 * resources found in resource bundles.</li>
 * </ul>
 *
 * @see Resources
 * @see UsesBundles
 * @see ResourceConverter
 * @see InstanceInjector
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(ResourceInjectorImpl.class)
public interface ResourceInjector
{
	/**
	 * Inject properties from resource bundles into a GUI component, using its
	 * {@code name} as a key to discover the properties to be injected. This
	 * method only injects {@code component} itself and doesn't care about its 
	 * children (it {@code component} is a {@link java.awt.Container}).
	 * <p/>
	 * If you want to inject a component and its children, then use 
	 * {@link #injectHierarchy}.
	 * 
	 * @param component the GUI component to be injected
	 */
	public void injectComponent(Component component);
	
	/**
	 * Inject properties from resource bundles into a GUI component, using its
	 * {@code name} as a key to discover the properties to be injected. This
	 * method also injects all children of {@code component} if it does have 
	 * children (it must be a {@link java.awt.Container}).
	 * 
	 * @param component the GUI component to be injected, including its children
	 */
	public void injectHierarchy(Component component);
	
	/**
	 * Inject properties from resource bundles into an instance of a given type, 
	 * using its class simple name (as returned by {@link java.lang.Class#getSimpleName()})
	 * as a key to discover the properties to be injected.
	 * <p/>
	 * Only writable java bean properties get injected, provided a matching value can
	 * be found in the chain of resource bundles.
	 * 
	 * @param <T> the type of the object to be injected
	 * @param instance the object to be injected
	 */
	public <T> void injectInstance(T instance);
	
	/**
	 * Inject properties from resource bundles into an instance of a given type, 
	 * using {@code name} as a key to discover the properties to be injected.
	 * <p/>
	 * Only writable java bean properties get injected, provided a matching value can
	 * be found in the chain of resource bundles.
	 * 
	 * @param <T> the type of the object to be injected
	 * @param instance the object to be injected
	 * @param name the name to be used as a key when searching properties for this
	 * object in the resource bundles
	 */
	public <T> void injectInstance(T instance, String name);
	
	/**
	 * Sets the new {@link Locale} to be used for next resource injections.
	 * <p/>
	 * This calls {@link Locale#setDefault(Locale)}.
	 * <p/>
	 * Cached resource bundles (containing properties expressed in previous
	 * {@code Locale}) are cleared and updated.
	 * <p/>
	 * An event is generated and pushed to {@code Channel<Locale>}. You can
	 * hence possibly listen to that events by annotating one of your class methods 
	 * with {@link net.guts.event.Consumes}:
	 * <pre>
	 * &#64;Consumes public void localeChanged(Locale newLocale)
	 * {
	 *     // Do whatever you need to do when Locale changes
	 * }
	 * </pre>
	 * <p/>
	 * Note that {@link net.guts.gui.application.WindowController} already listens 
	 * to {@code Locale} changes and forces a new resource injection to all currently
	 * visible Windows.
	 * 
	 * @param locale the new {@code Locale} to set as default for next resource 
	 * injections
	 */
	public void setLocale(Locale locale);
}
