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
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Nullable;

@Singleton
class ResourceBundleRegistryImpl implements ResourceBundleRegistry
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(ResourceBundleRegistryImpl.class);

	@Inject
	ResourceBundleRegistryImpl(Map<TypeLiteral<?>, ResourceConverter<?>> converters,
		@RootBundle @Nullable String root)
	{
		_converters = converters;
		_root = root;
	}
	
	// TODO Add own resourcebundle control to deal with Locale fallback correctly?
	// Or fully remove ResourceBundle and replace by own mechanism?
	private List<ResourceBundle> convertNamesToBundles(List<String> names)
	{
		// Check all bundles exist in classpath
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
		for (String name: names)
		{
			try
			{
				ResourceBundle bundle = ResourceBundle.getBundle(name);
				bundles.add(bundle);
			}
			catch (MissingResourceException e)
			{
				String msg = String.format("Bundle `%1$s` doesn't exist.", name);
				_logger.warn(msg, e);
			}
		}
		return bundles;
	}

	@Override public ResourceMap getBundle(Class<? extends Component> component)
	{
		// Ask for the sorted list of ResourceBundle matching the component type
		List<String> bundles = getBundleNames(component);
		// Add main application bundle
		if (_root != null)
		{
			bundles.add(_root);
		}

		// Build Map<String, String> values from all bundles
		List<ResourceBundle> resourceBundles = convertNamesToBundles(bundles);
		NavigableMap<String, String> values = new TreeMap<String, String>();
		for (ResourceBundle bundle: resourceBundles)
		{
			for (String key: bundle.keySet())
			{
				values.put(key, bundle.getString(key));
			}
		}
		return new ResourceMapImpl(values, _converters);
	}
	
	private List<String> getBundleNames(Class<?> type)
	{
		List<String> bundles = _bundlesPerClass.get(type);
		if (bundles == null)
		{
			// If type has @UsesBundles annotation,process it
			bundles = extractBundles(type.getAnnotation(UsesBundles.class));
			_bundlesPerClass.put(type, bundles);
		}
		if (bundles == NO_DEF_BUNDLE)
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
		// Always return a copy of the original list (to be modified by caller
		return new ArrayList<String>(bundles);
	}
	
	static private List<String> extractBundles(UsesBundles uses)
	{
		List<String> bundles = NO_DEF_BUNDLE;
		if (uses != null)
		{
			bundles = new ArrayList<String>();
			for (Class<?> name: uses.value())
			{
				String bundle = Resources.bundlePath(name);
				if (bundle != null && !bundles.contains(bundle))
				{
					bundles.add(bundle);
				}
			}
			Collections.reverse(bundles);
		}
		return bundles;
	}

	static final private List<String> NO_DEF_BUNDLE = Collections.emptyList();
	final private String _root;
	final private Map<Class<?>, List<String>> _bundlesPerClass = 
		new HashMap<Class<?>, List<String>>();
	final private Map<Package, List<String>> _bundlesPerPackage = 
		new HashMap<Package, List<String>>();
	final private Map<TypeLiteral<?>, ResourceConverter<?>> _converters;
}
