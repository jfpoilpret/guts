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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;

//TODO remove XStream-based prototype later on
@Singleton
class StorageManagerImpl implements StorageManager
{
	@Inject
	StorageManagerImpl(StorageMedium storage)
	{
		_storage = storage;
	}

	@Override public void load(String id, Object content)
	{
		byte[] rawData = _storage.load(id);
		if (rawData != null)
		{
			String xml = new String(rawData);
			// Deserialize XML into content
			_xstream.fromXML(xml, content);
		}
	}

	@Override public void save(String id, Object content)
	{
		// Serialize content into XML
		String xml = _xstream.toXML(content);
		_storage.save(id, xml.getBytes());
	}
	
	final private StorageMedium _storage;
	final private XStream _xstream = new XStream();
}
