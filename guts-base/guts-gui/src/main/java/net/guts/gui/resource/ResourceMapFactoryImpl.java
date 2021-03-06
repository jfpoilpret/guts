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

import net.guts.common.type.TypeHelper;
import net.guts.event.Consumes;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class ResourceMapFactoryImpl implements ResourceMapFactory
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(ResourceMapFactoryImpl.class);

	@Inject ResourceMapFactoryImpl(ResourceConverterFinder finder,
		ResourcePreprocessor preprocessor,
		@BindBundle Map<Class<?>, List<String>> classBundles,
		@BindBundle Map<String, List<String>> packageBundles,
		RootBundleHolder rootHolder)
	{
		if (rootHolder._root == null)
		{
			// Log this fact because it is likely to be a developer's mistake, but not
			// necessarily.
			_logger.debug(
				"There is no root bundle defined (with Resources.bindRootBundle()); " +
				"hence ResourceInjector will work only for classes or packages " +
				"annotated with @UsesBundles.");
		}
		_finder = finder;
		_preprocessor = preprocessor;
		_root = getBundle(rootHolder._root);
		for (Map.Entry<Class<?>, List<String>> entry: classBundles.entrySet())
		{
			_bundlesPerClass.put(entry.getKey(), getBundles(entry.getValue()));
		}
		for (Map.Entry<String, List<String>> entry: packageBundles.entrySet())
		{
			_bundlesPerPackage.put(entry.getKey(), getBundles(entry.getValue()));
		}
	}
	
	@Override public ResourceMap createResourceMap(Class<?> clazz)
	{
		// Ask for the sorted list of Bundle matching the component type
		List<Bundle> bundles = getBundleNames(clazz);
		return new ResourceMap(bundles, _finder, _preprocessor);
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
			String pack = TypeHelper.getPackage(type);
			bundles = _bundlesPerPackage.get(pack);
			if (bundles == null)
			{
				Package classPackage = type.getPackage();
				if (classPackage != null)
				{
					// If the whole package has UsesBundles annotation, process it
					bundles = extractBundles(
						type, classPackage.getAnnotation(UsesBundles.class));
					_bundlesPerPackage.put(pack, bundles);
				}
			}
		}
		return bundles;
	}
	
	private List<Bundle> extractBundles(Class<?> origin, UsesBundles uses)
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
				addBundle(bundles, origin, path);
			}
			// If uses.value() is empty, then take current class/package as bundle
			if (uses.value().length == 0)
			{
				// and use "resources" or the class name as default name
				String bundleName = 
					(uses.useClassName() ? origin.getSimpleName() : DEFAULT_BUNDLE_NAME);
				addBundle(bundles, origin, bundleName);
			}
		}
		return bundles;
	}
	
	private void addBundle(List<Bundle> bundles, Class<?> origin, String path)
	{
		String realPath = Bundle.checkBundleExists(path, origin);
		if (realPath != null && !bundles.contains(new Bundle(realPath)))
		{
			Bundle bundle = getBundle(realPath);
			if (bundle != null)
			{
				bundles.add(bundle);
			}
		}
	}

	private List<Bundle> getBundles(List<String> fullPathBundles)
	{
		List<Bundle> bundles = new ArrayList<Bundle>(fullPathBundles.size());
		if (_root != null)
		{
			bundles.add(_root);
		}
		for (String bundle: fullPathBundles)
		{
			bundles.add(getBundle(bundle));
		}
		return bundles;
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

	// Workaround to Guice limitation on injection of optional args to constructors
	// See http://code.google.com/p/google-guice/wiki/FrequentlyAskedQuestions
	// "How can I inject optional parameters into a constructor?"
	//CSOFF: VisibilityModifierCheck
	static class RootBundleHolder
	{
		@Inject(optional = true) @BindBundle String _root;
	}
	//CSON: VisibilityModifierCheck
	
	static final private String DEFAULT_BUNDLE_NAME = "resources";
	
	final private Bundle _root;
	final private Map<String, Bundle> _bundles = new HashMap<String, Bundle>();
	final private Map<Class<?>, List<Bundle>> _bundlesPerClass = 
		new HashMap<Class<?>, List<Bundle>>();
	final private Map<String, List<Bundle>> _bundlesPerPackage = 
		new HashMap<String, List<Bundle>>();
	final private ResourceConverterFinder _finder;
	final private ResourcePreprocessor _preprocessor;
}
