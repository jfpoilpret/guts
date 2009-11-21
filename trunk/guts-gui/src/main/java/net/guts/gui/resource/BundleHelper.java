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

final class BundleHelper
{
	static final private Logger _logger = LoggerFactory.getLogger(BundleHelper.class);
	
	private BundleHelper()
	{
	}

	static String checkBundleExists(String path, Class<?> origin)
	{
		// Check the path exists
		String realPath = path;
		if (path.startsWith("/"))
		{
			realPath = realPath.substring(1);
		}
		else
		{
			//FIXME problems with embedded classes!
			String pack = origin.getName();
			pack = pack.substring(0, pack.lastIndexOf(".")).replaceAll("\\.", "/");
			realPath = pack + "/" + realPath;
		}

		URL url = Thread.currentThread().getContextClassLoader().getResource(
			realPath + ".properties");
		if (url != null)
		{
			return realPath;
		}
		if (_logger.isWarnEnabled())
		{
			if (origin != null)
			{
				_logger.warn("Bundle `{}` doesn't exist in context of class {}", 
					path, origin.getName());
			}
			else
			{
				_logger.warn("Bundle `{}` doesn't exist", path);
			}
		}
		return null;
	}
}
