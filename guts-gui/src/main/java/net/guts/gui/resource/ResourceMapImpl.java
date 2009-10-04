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
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;

final class ResourceMapImpl implements ResourceMap
{
	static final private Logger _logger = LoggerFactory.getLogger(ResourceMapImpl.class);

	ResourceMapImpl(NavigableMap<String, ResourceEntry> resources, 
		Map<TypeLiteral<?>, ResourceConverter<?>> converters)
	{
		_resources = resources;
		_converters = converters;
	}

	@SuppressWarnings("unchecked") @Override 
	public <T> T getValue(Key key, TypeLiteral<T> type)
	{
		ResourceConverter<T> converter = 
			(ResourceConverter<T>) _converters.get(type);
		if (converter == null)
		{
			String msg = String.format(
				"getValue(prefix = `%s`, key = `%s`) can't find converter for type %s", 
				key.prefix(), key.key(), type);
			_logger.info(msg);
			return null;
		}
		ResourceEntry value = _resources.get(key.prefix() + "." + key.key());
		if (value == null)
		{
			return null;
		}
		return converter.convert(value);
	}

	@Override public <T> T getValue(Key key, Class<T> type)
	{
		return getValue(key, TypeLiteral.get(type));
	}

	@Override public Set<Key> keys(String prefix)
	{
		return new KeySet(
			prefix, _resources.navigableKeySet().subSet(prefix + ".", prefix + "/"));
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
				return new KeyImpl(next.substring(_fullPrefix.length()), _prefix);
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
	
	static final private class KeyImpl implements Key
	{
		private KeyImpl(String key, String prefix)
		{
			_key = key;
			_prefix = prefix;
		}

		@Override public String key()
		{
			return _key;
		}

		@Override public String prefix()
		{
			return _prefix;
		}
		
		final private String _key;
		final private String _prefix;
	}
	
	final private NavigableMap<String, ResourceEntry> _resources;
	final private Map<TypeLiteral<?>, ResourceConverter<?>> _converters;
}
