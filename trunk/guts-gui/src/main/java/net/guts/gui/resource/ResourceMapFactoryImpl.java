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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.event.Consumes;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.internal.Nullable;

@Singleton
class ResourceMapFactoryImpl implements ResourceMapFactory
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(ResourceMapFactoryImpl.class);

	@Inject
	ResourceMapFactoryImpl(ResourceConverterFinder finder,
		@RootBundle @Nullable String root)
	{
		if (root == null)
		{
			// Log this fact because it is likely to be a developer's mistake, but not
			// necessarily.
			_logger.debug(
				"There is no root bundle defined (with Resources.bindRootBundle()); " +
				"hence ResourceInjector will work only for classes or packages " +
				"annotated with @UsesBundles.");
		}
		_finder = finder;
		_root = getBundle(root);
	}
	
	@Override public ResourceMap createResourceMap(Class<?> clazz)
	{
		// Ask for the sorted list of Bundle matching the component type
		List<Bundle> bundles = getBundleNames(clazz);
		return new ResourceMapImpl(bundles, _finder);
	}
	
	private List<Bundle> getBundleNames(Class<?> type)
	{
		List<Bundle> bundles = _bundlesPerClass.get(type);
		if (bundles == null)
		{
			// If type has @UsesBundles annotation,process it
			bundles = extractBundles(type, type.getAnnotation(UsesBundles.class));
			_bundlesPerClass.put(type, bundles);
		}

		if (bundles.size() == 0 || (bundles.size() == 1 && _root != null))
		{
			// If there is no bundles dependency defined at class level, check package level
			Package pack = type.getPackage();
			if (pack != null)
			{
				bundles = _bundlesPerPackage.get(pack);
				if (bundles == null)
				{
					// If the whole package has UsesBundles annotation, process it
					bundles = extractBundles(type, pack.getAnnotation(UsesBundles.class));
					_bundlesPerPackage.put(pack, bundles);
				}
			}
		}
		return bundles;
	}
	
	private List<Bundle> extractBundles(Class<?> type, UsesBundles uses)
	{
		List<Bundle> bundles = new ArrayList<Bundle>();
		if (_root != null)
		{
			bundles.add(_root);
		}
		if (uses != null)
		{
			for (String path: uses.value())
			{
				addBundle(bundles, type, path);
			}
			// If uses.value() is empty, then take current class/package as bundle
			if (uses.value().length == 0)
			{
				//TODO default name? or class name?
				addBundle(bundles, type, "resources");
			}
		}
		return bundles;
	}
	
	private void addBundle(List<Bundle> bundles, Class<?> origin, String path)
	{
		String realPath = BundleHelper.checkBundleExists(path, origin);
		if (realPath != null && !bundles.contains(realPath))
		{
			Bundle bundle = getBundle(realPath);
			if (bundle != null)
			{
				bundles.add(bundle);
			}
		}
	}

	private Bundle getBundle(String path)
	{
		if (path == null)
		{
			return null;
		}
		Bundle bundle = _bundles.get(path);
		if (bundle != null)
		{
			return bundle;
		}
		// Create a new Bundle and cache it
		bundle = new Bundle(path);
		bundle.update(Locale.getDefault());
		_bundles.put(path, bundle);
		return bundle;
	}

	@Consumes(priority = Integer.MIN_VALUE) 
	public void localeChanged(Locale locale)
	{
		// Force refresh of all Bundles
		for (Bundle bundle: _bundles.values())
		{
			bundle.update(locale);
		}
	}

	final private Bundle _root;
	final private Map<String, Bundle> _bundles = new HashMap<String, Bundle>();
	final private Map<Class<?>, List<Bundle>> _bundlesPerClass = 
		new HashMap<Class<?>, List<Bundle>>();
	final private Map<Package, List<Bundle>> _bundlesPerPackage = 
		new HashMap<Package, List<Bundle>>();
	final private ResourceConverterFinder _finder;
}
