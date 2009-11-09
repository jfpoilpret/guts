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

final class ResourceMapImpl implements ResourceMap
{
	static final private Logger _logger = LoggerFactory.getLogger(ResourceMapImpl.class);

	ResourceMapImpl(List<Bundle> bundles, ResourceConverterFinder finder)
	{
		_bundles = bundles;
		for (Bundle bundle: bundles)
		{
			_allKeys.addAll(bundle.properties().keySet());
		}
		_finder = finder;
	}

	@Override public <T> T getValue(Key key, TypeLiteral<T> type)
	{
		ResourceConverter<T> converter = _finder.getConverter(type);
		if (converter == null)
		{
			_logger.debug(
				"getValue(prefix = `{}`, key = `{}`) can't find converter for type {}",
				new Object[]{key.prefix(), key.name(), type});
			return null;
		}
		String fullKey = key.prefix() + "." + key.name();
		if (!_allKeys.contains(fullKey))
		{
			return null;
		}
		for (Bundle bundle: _bundles)
		{
			if (bundle.properties().containsKey(fullKey))
			{
				ResourceEntry entry = new ResourceEntry(
					bundle.properties().get(fullKey), bundle.source());
				return converter.convert(entry);
			}
		}
		// Cannot reach this location normally
		return null;
	}

	@Override public <T> T getValue(Key key, Class<T> type)
	{
		return getValue(key, TypeLiteral.get(type));
	}

	@Override public Set<Key> keys(String prefix)
	{
		return new KeySet(prefix, _allKeys.subSet(prefix + ".", prefix + "/"));
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

		@Override public String name()
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
	
	final private List<Bundle> _bundles;
	final private NavigableSet<String> _allKeys = new TreeSet<String>();
	final private ResourceConverterFinder _finder;
}
