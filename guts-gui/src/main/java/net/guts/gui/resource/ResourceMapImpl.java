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

	ResourceMapImpl(NavigableMap<String, String> resources, 
		Map<TypeLiteral<?>, ResourceConverter<?>> converters)
	{
		_resources = resources;
		_converters = converters;
	}

	@SuppressWarnings("unchecked") @Override 
	public <T> T getValue(String prefix, String key, TypeLiteral<T> type)
	{
		ResourceConverter<T> converter = 
			(ResourceConverter<T>) _converters.get(type);
		if (converter == null)
		{
			String msg = String.format(
				"getValue(prefix = `%s`, key = `%s`) can't find converter for type %s", 
				prefix, key, type);
			_logger.info(msg);
			return null;
		}
		String value = _resources.get(prefix + "." + key);
		if (value == null)
		{
			return null;
		}
		return converter.convert(value);
	}

	@Override public <T> T getValue(String prefix, String key, Class<T> type)
	{
		return getValue(prefix, key, TypeLiteral.get(type));
	}

	@Override public Set<String> keys(String prefix)
	{
		return new KeySet(
			prefix, _resources.navigableKeySet().subSet(prefix + ".", prefix + "/"));
	}

	// The classes below are used to dynamically remove prefix from the property keys
	// set returned by keys(prefix) method.
	static private class KeySet extends AbstractSet<String>
	{
		KeySet(String prefix, Set<String> keys)
		{
			_prefix = prefix + ".";
			_keys = keys;
		}

		@Override public Iterator<String> iterator()
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
	
	static private class KeyIterator implements Iterator<String>
	{
		KeyIterator(String prefix, Iterator<String> iterator)
		{
			_prefix = prefix;
			_iterator = iterator;
		}
		
		@Override public boolean hasNext()
		{
			return _iterator.hasNext();
		}

		@Override public String next()
		{
			String next = _iterator.next();
			if (next.startsWith(_prefix))
			{
				return next.substring(_prefix.length());
			}
			else
			{
				return next;
			}
		}

		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		final private String _prefix;
		final private Iterator<String> _iterator;
	}
	
	final private NavigableMap<String, String> _resources;
	final private Map<TypeLiteral<?>, ResourceConverter<?>> _converters;
}
