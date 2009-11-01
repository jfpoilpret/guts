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

import java.util.Set;

import com.google.inject.TypeLiteral;

/**
 * Map of resource properties, sourced in a given set of resource bundles.
 * This is created by {@link ResourceInjector} before calling 
 * {@link InstanceInjector#inject} to inject an instance of a given class;
 * The created {@code ResourceMap} contains the properties of all the relevant
 * resource bundles for that class, i.e. bundles declared by {@link UsesBundles}
 * annotation used by that class or its package, in addition to the "root bundle"
 * defined through {@link Resources#bindRootBundle}.
 * <p/>
 * {@code ResourceMap} is the main information source that {@link InstanceInjector}
 * receives for injecting an instance; it can be used to filter out all
 * properties relevant to a given prefix, and then retrieving the value of any
 * property in a given type.
 * <p/>
 * You don't need to use this interface unless you need to write your own 
 * {@link InstanceInjector}.
 *
 * @author Jean-Francois Poilpret
 */
public interface ResourceMap
{
	/**
	 * Returns all resource keys in {@code this} ResourceMap, starting with prefix. 
	 * {@code prefix} will be searched in property names like {@code "prefix.name"};
	 * note the presence of the dot after {@code prefix}, without it, a given property
	 * is not retained in the returned keys.
	 * <p/>
	 * For a GUI component, {@code prefix} is its name as set by 
	 * {@link java.awt.Component#setName}; for other objects, it is, by default, the
	 * name of that object class, but it can be different if explicitly required in
	 * {@link ResourceInjector#injectInstance(Object, String)}.
	 * 
	 * @param prefix the prefix on which to filter out the returned set of keys
	 * @return the set of keys that match {@code prefix} in {@code this} ResourceMap
	 */
	public Set<Key> keys(String prefix);

	/**
	 * Retrieves the value of the property with the given {@code key}.
	 * The original {@code String} value from the resource bundle is converted
	 * into the given {@code type} through the most suitable {@link ResourceConverter}
	 * which must have been first registered with 
	 * {@link Resources#bindConverter(com.google.inject.Binder, Class)}.
	 * If there is no relevant {@code ResourceConverter} or if the original
	 * {@code String} value cannot be converted to {@code type}, then {@code null}
	 * is returned and an error is logged through SLF4J.
	 * 
	 * @param <T> type to which the original {@String} property value should be
	 * converted
	 * @param key unique identifier of the resource to be retrieved
	 * @param type type to which the original {@String} property value should be
	 * converted
	 * @return the value of the resource identified by {@code key} converted to
	 * {@code type}; {@code null} will be returned if there is no property with 
	 * {@code key} or if ithat property cannot be converted to {@code type}.
	 */
	public <T> T getValue(Key key, Class<T> type);

	/**
	 * Retrieves the value of the property with the given {@code key}.
	 * The original {@code String} value from the resource bundle is converted
	 * into the given {@code type} through the most suitable {@link ResourceConverter}
	 * which must have been first registered with 
	 * {@link Resources#bindConverter(com.google.inject.Binder, Class)}.
	 * If there is no relevant {@code ResourceConverter} or if the original
	 * {@code String} value cannot be converted to {@code type}, then {@code null}
	 * is returned and an error is logged through SLF4J.
	 * 
	 * @param <T> type to which the original {@String} property value should be
	 * converted
	 * @param key unique identifier of the resource to be retrieved
	 * @param type type to which the original {@String} property value should be
	 * converted
	 * @return the value of the resource identified by {@code key} converted to
	 * {@code type}; {@code null} will be returned if there is no property with 
	 * {@code key} or if ithat property cannot be converted to {@code type}.
	 */
	public <T> T getValue(Key key, TypeLiteral<T> type);

	/**
	 * The unique identifier of a resource property in a {@code ResourceMap}.
	 *
	 * @author Jean-Francois Poilpret
	 */
	static public interface Key
	{
		/**
		 * Prefix part of {@code this} resource key. This is the same as the 
		 * {@code prefix} argument passed to {@link ResourceMap#keys(String)},
		 * from which this {@code Key} is issued.
		 * <p/>
		 * The prefix of a resource matches the name of an object to be injected
		 * with its resources.
		 * 
		 * @return the prefix of this key
		 */
		public String prefix();
		
		/**
		 * Name part of {@code this} resource key. This is the name of the bean
		 * property to be injected into an object.
		 * <p/>
		 * Used by {@link InstanceInjector}s to find out which method to call
		 * to set the property value in the objects they must inject resources 
		 * into.
		 * 
		 * @return name of this key
		 */
		//TODO refactor to use "name"
		public String key();
	}
}
