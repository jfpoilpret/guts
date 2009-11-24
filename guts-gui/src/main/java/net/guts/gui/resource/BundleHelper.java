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

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.type.TypeHelper;

final class BundleHelper
{
	static final private Logger _logger = LoggerFactory.getLogger(BundleHelper.class);
	
	private BundleHelper()
	{
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
}
