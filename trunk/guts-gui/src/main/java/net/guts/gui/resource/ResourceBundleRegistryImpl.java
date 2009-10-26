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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.NavigableMap;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.internal.Nullable;

@Singleton
class ResourceBundleRegistryImpl implements ResourceBundleRegistry
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(ResourceBundleRegistryImpl.class);

	@Inject
	ResourceBundleRegistryImpl(ResourceConverterFinder finder,
		@RootBundle @Nullable String root)
	{
		_finder = finder;
		_root = getBundle(root);
	}
	
	@Override public ResourceMap getBundle(Class<?> clazz)
	{
		// Ask for the sorted list of ResourceBundle matching the component type
		List<Bundle> bundles = getBundleNames(clazz);

		NavigableMap<String, ResourceEntry> values = new TreeMap<String, ResourceEntry>();
		for (Bundle bundle: bundles)
		{
			for (String key: bundle.bundle().keySet())
			{
				values.put(key, 
					new ResourceEntry(bundle.bundle().getString(key), bundle.source()));
			}
		}
		return new ResourceMapImpl(values, _finder);
	}
	
	private List<Bundle> getBundleNames(Class<?> type)
	{
		List<Bundle> bundles = _bundlesPerClass.get(type);
		if (bundles == null)
		{
			// If type has @UsesBundles annotation,process it
			bundles = extractBundles(type.getAnnotation(UsesBundles.class));
			_bundlesPerClass.put(type, bundles);
		}
		//FIXME this test is incorrect when _root == null!
		if (bundles.size() <= 1)
		{
			// If there is no bundles dependency defined at class level, check package level
			Package pack = type.getPackage();
			if (pack != null)
			{
				bundles = _bundlesPerPackage.get(pack);
				if (bundles == null)
				{
					// If the whole package has UsesBundles annotation, process it
					bundles = extractBundles(pack.getAnnotation(UsesBundles.class));
					_bundlesPerPackage.put(pack, bundles);
				}
			}
		}
		return bundles;
	}
	
	private List<Bundle> extractBundles(UsesBundles uses)
	{
		List<Bundle> bundles = new ArrayList<Bundle>();
		if (uses != null)
		{
			for (Class<?> name: uses.value())
			{
				String bundlePath = Resources.bundlePath(name);
				if (bundlePath != null && !bundles.contains(bundlePath))
				{
					Bundle bundle = getBundle(bundlePath);
					if (bundle != null)
					{
						bundles.add(bundle);
					}
				}
			}
			//TODO if uses.value() is empty, then take current class/package as bundle
			Collections.reverse(bundles);
		}
		if (_root != null)
		{
			bundles.add(_root);
		}
		return bundles;
	}
	
	static private Bundle getBundle(String path)
	{
		if (path == null)
		{
			return null;
		}
		try
		{
			return new Bundle(path, ResourceBundle.getBundle(path + ".resources"));
		}
		catch (MissingResourceException e)
		{
			String msg = String.format("Bundle `%1$s` doesn't exist.", path + ".resources");
			_logger.warn(msg, e);
			return null;
		}
	}
	
	static private class Bundle
	{
		Bundle(String source, ResourceBundle bundle)
		{
			_source = source;
			_bundle = bundle;
		}
		
		String source()
		{
			return _source;
		}
		
		ResourceBundle bundle()
		{
			return _bundle;
		}
		
		@Override public int hashCode()
		{
			return _source.hashCode();
		}

		@Override public boolean equals(Object o)
		{
			if (!(o instanceof Bundle))
			{
				return false;
			}
			Bundle that = (Bundle) o;
			return this._source.equals(that._source);
		}

		final private String _source;
		final private ResourceBundle _bundle;
	}

	final private Bundle _root;
	final private Map<Class<?>, List<Bundle>> _bundlesPerClass = 
		new HashMap<Class<?>, List<Bundle>>();
	final private Map<Package, List<Bundle>> _bundlesPerPackage = 
		new HashMap<Package, List<Bundle>>();
	final private ResourceConverterFinder _finder;
}
