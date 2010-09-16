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

import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;

@Singleton
class XStreamSerializationManager implements SerializationManager
{
	@Override public byte[] serialize(Object object)
	{
		// Serialize content into XML
		String xml = _xstream.toXML(object);
		return xml.getBytes();
	}
	
	@Override public void deserialize(byte[] content, Object object)
	{
		if (content != null)
		{
			String xml = new String(content);
			_xstream.fromXML(xml, object);
		}
		
	}
	
	final private XStream _xstream = new XStream();
}
