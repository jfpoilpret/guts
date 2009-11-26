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

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
final public class ResourceMap
{
	static final private Logger _logger = LoggerFactory.getLogger(ResourceMap.class);

	ResourceMap(List<Bundle> bundles, ResourceConverterFinder finder, 
		ResourcePreprocessor preprocessor)
	{
		_bundles = bundles;
		for (Bundle bundle: bundles)
		{
			_allKeys.addAll(bundle.properties().keySet());
		}
		_finder = finder;
		_preprocessor = preprocessor;
	}

	/**
	 * Returns all resource keys in {@code this} ResourceMap, starting with {@code prefix}. 
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
	public Set<Key> keys(String prefix)
	{
		return new KeySet(prefix, _allKeys.subSet(prefix + ".", prefix + "/"));
	}
	
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
	 * @param <T> type to which the original {@code String} property value should be
	 * converted
	 * @param key unique identifier of the resource to be retrieved
	 * @param type type to which the original {@code String} property value should be
	 * converted
	 * @return the value of the resource identified by {@code key} converted to
	 * {@code type}; {@code null} will be returned if there is no property with 
	 * {@code key} or if ithat property cannot be converted to {@code type}.
	 */
	public <T> T getValue(Key key, TypeLiteral<T> type)
	{
		String fullKey = key.fullKey();
		ResourceConverter<T> converter = _finder.getConverter(type);
		if (converter == null)
		{
			_logger.debug("getValue(key = `{}`) can't find converter for type {}", 
				fullKey, type);
			return null;
		}
		if (!_allKeys.contains(fullKey))
		{
			return null;
		}
		for (Bundle bundle: _bundles)
		{
			if (bundle.properties().containsKey(fullKey))
			{
				// First pre-process raw string value
				String value = _preprocessor.convert(this, bundle.properties().get(fullKey));
				// Then require conversion
				ResourceEntry entry = new ResourceEntry(value, bundle.source());
				return converter.convert(entry);
			}
		}
		// Cannot reach this location normally
		_logger.error(
			"Impossible error! getValue() for key `{}` couldn't find property", fullKey);
		return null;
	}

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
	 * @param <T> type to which the original {@code String} property value should be
	 * converted
	 * @param key unique identifier of the resource to be retrieved
	 * @param type type to which the original {@code String} property value should be
	 * converted
	 * @return the value of the resource identified by {@code key} converted to
	 * {@code type}; {@code null} will be returned if there is no property with 
	 * {@code key} or if ithat property cannot be converted to {@code type}.
	 */
	public <T> T getValue(Key key, Class<T> type)
	{
		return getValue(key, TypeLiteral.get(type));
	}

	/**
	 * Get a general {@link Key} given a complete {@code name}. This allows to:
	 * <ul>
	 * <li>check that this {@code ResourceMap} contains a property which complete name
	 * is {@code name}</li>
	 * <li>obtain a {@link Key} referring to that property, whcih can then be used with other
	 * {@code ResourceMap} methods, like {@link #getValue(Key, Class)}</li>
	 * </ul>
	 * <p/>
	 * You normally won't need to use this method, except if you are writing your own
	 * implementation of {@link ResourcePreprocessor}.
	 * 
	 * @param name the full name of the property we want to look up in this 
	 * {@code ResourceMap}
	 * @return a new {@code Key} that points to property with name {@code name}, or 
	 * {@code null} if there is no property named {@code name}
	 */
	public Key getKey(String name)
	{
		if (_allKeys.contains(name))
		{
			return new Key(name, null);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * The unique identifier of a resource property in a {@code ResourceMap}.
	 *
	 * @author Jean-Francois Poilpret
	 */
	static final public class Key
	{
		private Key(String key, String prefix)
		{
			_key = key;
			_prefix = prefix;
		}

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
		public String name()
		{
			return _key;
		}

		private String fullKey()
		{
			return (_prefix == null ? "" : _prefix + ".") + _key;
		}
		
		final private String _key;
		final private String _prefix;
	}
	
	// The classes below are used to dynamically remove prefix from the property keys
	// set returned by keys(prefix) method.
	static private class KeySet extends AbstractSet<Key>
	{
		KeySet(String prefix, Set<String> keys)
		{
			_prefix = prefix;
			_keys = keys;
		}
		
		@Override public Iterator<Key> iterator()
		{
			return new KeyIterator(_prefix, _keys.iterator());
		}

		@Override public int size()
		{
			return _keys.size();
		}

		private final String _prefix;
		private final Set<String> _keys;
	}
	
	static private class KeyIterator implements Iterator<Key>
	{
		KeyIterator(String prefix, Iterator<String> iterator)
		{
			_prefix = prefix;
			_fullPrefix = _prefix + ".";
			_iterator = iterator;
		}
		
		@Override public boolean hasNext()
		{
			return _iterator.hasNext();
		}

		@Override public Key next()
		{
			String next = _iterator.next();
			if (next.startsWith(_fullPrefix))
			{
				return new Key(next.substring(_fullPrefix.length()), _prefix);
			}
			else
			{
				return null;
			}
		}

		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		final private String _prefix;
		final private String _fullPrefix;
		final private Iterator<String> _iterator;
	}
	
	final private List<Bundle> _bundles;
	final private NavigableSet<String> _allKeys = new TreeSet<String>();
	final private ResourceConverterFinder _finder;
	final private ResourcePreprocessor _preprocessor;
}
