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

/**
 * Defines a resource injector that is specialized in resource injection for
 * instances of a given type.
 * <p/>
 * {@code InstanceInjector}s are used by {@link ResourceInjector} to inject 
 * object and GUI components. {@code ResourceInjector} selects the best 
 * {@code InstanceInjector} for a given object, based on the type of this object:
 * it first looks for an {@code InstanceInjector} that directly handles the class
 * of the object; if none is found, then it takes its superclass and searches again
 * a matching {@code InstanceInjector}; it continues until one suitable
 * {@code InstanceInjector} is found.
 * <p/>
 * There is one {@code InstanceInjector} that is suitable for {@code Object.class} and
 * that will be used if no more specific {@code InstanceInjector} can be found. It
 * simply uses java bean conventions (property setters) to inject resources.
 * <p/>
 * You can create and register your own {@code InstanceInjector} if you have special
 * injection needs for a given class (in particular for special Swing or 3rd-party
 * GUI components), where the default bean setter injection can't work.
 * <p/>
 * If you need special support for mnemonics, then your {@code InstanceInjector}
 * can make use of {@link MnemonicInfo} utility prior to injection.
 * <p/>
 * You need to register your specific {@code InstanceInjector}s; for that, you
 * would use {@link Resources#bindInstanceInjector} in one of your Guice
 * {@link com.google.inject.Module}s:
 * <pre>
 * Resources.bindInstanceInjector(binder(), MyType.class).to(MyTypeInjector.class);
 * </pre>
 * <p/>
 * You may choose to derive your own {@code InstanceInjector} implementations from
 * {@link BeanPropertiesInjector} class, in order to reduce the quantity of code to
 * the strict minimum.
 * <p/>
 * Guts-GUI already provides specific {@code InstanceInjector} implementations for
 * common Swing components: {@code AbstractButton}, {@code JLabel}, 
 * {@code JFileChooser}... You can find the exhaustive list in {@link ResourceModule}
 * documentation.
 *  
 * @param <T> the type supported by this {@code InstanceInjector}; any instance of a
 * {@code T} subtype should be supported as well (maybe with some limitations).
 *
 * @author Jean-Francois Poilpret
 */
public interface InstanceInjector<T>
{
	/**
	 * Injects resources, listed in {@code resources}, into the given {@code instance}.
	 * It must use {@code resources} to iterate through each relevant properties for
	 * {@code instance}.
	 * <p/>
	 * Since {@code resources} contain all properties from the resource bundles used
	 * by the class of {@code instance}, the {@code instance} name, {@code prefix},
	 * must be used to filter out only relevant properties.
	 * <p/>
	 * Implementations of this method should iterate through {@code resources} and
	 * inject each individual resource into {@code instance}. {@link ResourceMap}
	 * provided type-safe methods to get properties to be injected into 
	 * {@code instance}.
	 * 
	 * @param instance the object to be injected with resources
	 * @param prefix the name to give to {@code instance}, to consider only
	 * relevant resources properties from {@code resources}
	 * @param resources the list of all resources available from the resource bundles
	 * used by the class of {@code instance} (as defined by the {@link UsesBundles}
	 * annotation on that class, or as arguments passed to {@link Resources#bindClassBundles}
	 * called for that class)
	 */
	public void inject(T instance, String prefix, ResourceMap resources);
}
