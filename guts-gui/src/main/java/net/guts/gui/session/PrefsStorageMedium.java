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
import com.thoughtworks.xstream.XStream;

//TODO remove XStream-based prototype later on
@Singleton
class PrefsStorageMedium implements StorageMedium
{
	static final private Logger _logger = LoggerFactory.getLogger(PrefsStorageMedium.class);

	@Inject
	PrefsStorageMedium(@BindSessionApplication Class<?> applicationNode)
	{
		_root = Preferences.userNodeForPackage(applicationNode);
	}

	@Override public void load(String id, Object content)
	{
		String xml = _root.get(id, null);
		if (xml == null)
		{
			_logger.debug("load({}): nothing found in Backing Store.", id);
			return;
		}
		// Deserialize XML into content
		_xstream.fromXML(xml, content);
	}

	@Override public void save(String id, Object content)
	{
		try
		{
			// Serialize content into XML
			String xml = _xstream.toXML(content);
			_root.put(id, xml);
			_root.flush();
		}
		catch (BackingStoreException e)
		{
			_logger.warn("Error saving state id " + id, e);
		}
	}
	
	final private Preferences _root;
	final private XStream _xstream = new XStream();
}
