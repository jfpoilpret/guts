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

package net.guts.gui.session;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class PrefsStorageMedium implements StorageMedium
{
	static final private Logger _logger = LoggerFactory.getLogger(PrefsStorageMedium.class);

	@Inject
	PrefsStorageMedium(@BindSessionApplication Class<?> applicationNode)
	{
		_root = Preferences.userNodeForPackage(applicationNode);
	}

	@Override public String checkName(String name)
	{
		if (name.length() > Preferences.MAX_KEY_LENGTH)
		{
			return KEY_LENGTH_ERROR;
		}
		else
		{
			return null;
		}
	}
	
	@Override public byte[] load(String name)
	{
		byte[] content = _root.getByteArray(name, null);
		if (content == null)
		{
			_logger.debug("load({}): nothing found in Backing Store.", name);
		}
		return content;
	}

	@Override public void save(String name, byte[] content)
	{
		try
		{
			_root.putByteArray(name, content);
			_root.flush();
		}
		catch (BackingStoreException e)
		{
			_logger.warn("Error saving state id " + name, e);
		}
	}

	static final private String KEY_LENGTH_ERROR = String.format(
		"java.util.prefs package allows key names of max %d characters.",
		Preferences.MAX_KEY_LENGTH);
	final private Preferences _root;
}
