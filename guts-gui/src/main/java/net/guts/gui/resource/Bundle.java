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

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.type.TypeHelper;

final class Bundle
{
	static private final Logger _logger = LoggerFactory.getLogger(Bundle.class);

	Bundle(String source)
	{
		_path = source;
		_source = source.substring(0, source.lastIndexOf("/"));
	}
	
	void update(Locale locale)
	{
		if (_locale == null || !_locale.equals(locale))
		{
			// Clear current cache (because Locale has changed)
			_properties.clear();
			// Find each properties set from least to most specific Locale 
			// and add them to the list of all properties
			String path = _path;
			readProperties(path);
			path += "_" + locale.getLanguage();
			readProperties(path);
			if (!"".equals(locale.getCountry()))
			{
				path += "_" + locale.getCountry();
				readProperties(path);
				if (!"".equals(locale.getVariant()))
				{
					path += "_" + locale.getVariant();
					readProperties(path);
				}
			}
			_locale = locale;
		}
	}
	
	String source()
	{
		return _source;
	}
	
	@SuppressWarnings("unchecked") 
	Map<String, String> properties()
	{
		return (Map<String, String>) (Map<?, ?>) _properties;
	}

	//TODO add support for XML-based properties files?
	private void readProperties(String path)
	{
		InputStream in = Thread.currentThread().getContextClassLoader()
			.getResourceAsStream(path + ".properties");
		if (in == null)
		{
			_logger.debug("Bundle `{}.properties` doesn't exist", path); 
			return;
		}
		// CSOFF: IllegalCatchCheck
		// catch Exception allowed because both IOException and IllegalArgumentException
		// can occur but the handling code is exactly the same.
		try
		{
			_properties.load(in);
		}
		catch (Exception e)
		{
			String msg = String.format("Bundle `{}.properties` couldn't be loaded", path);
			_logger.warn(msg, e); 
		}
		// CSON: IllegalCatchCheck
	}
	
	@Override public int hashCode()
	{
		return _path.hashCode();
	}

	@Override public boolean equals(Object o)
	{
		if (!(o instanceof Bundle))
		{
			return false;
		}
		Bundle that = (Bundle) o;
		return this._path.equals(that._path);
	}

	static String checkBundleExists(String path, Class<?> origin)
	{
		// Build the full path
		String sourcePath = null;
		if (origin != null)
		{
			sourcePath = TypeHelper.getPackagePath(origin);
		}
		// Check the path exists
		String realPath = null;
		if (path.startsWith("/"))
		{
			realPath = path.substring(1);
		}
		else
		{
			realPath = sourcePath + "/" + path;
		}
	
		// Check the path exists
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(realPath + ".properties");
		if (url != null)
		{
			return realPath;
		}
		if (_logger.isWarnEnabled())
		{
			if (sourcePath != null)
			{
				_logger.warn("Bundle `{}` doesn't exist in context of {}", 
					path, origin.getSimpleName());
			}
			else
			{
				_logger.warn("Bundle `{}` doesn't exist", path);
			}
		}
		return null;
	}

	private Locale _locale;
	final private String _path;
	final private String _source;
	final private Properties _properties = new Properties();
}
