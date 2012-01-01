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

import java.nio.charset.Charset;

import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

@Singleton
class XStreamSerializationManager implements SerializationManager
{
	// http://kenai.com/jira/browse/GUTS-44
	// UTF-8 is always present:
	// http://download.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html
	static private final Charset UTF_8 = Charset.forName("UTF-8");

	@Override
	public byte[] serialize(Object object)
	{
		// Serialize content into XML
		// http://kenai.com/jira/browse/GUTS-50
		_xstream.processAnnotations(object.getClass());
		String xml = _xstream.toXML(object);
		return xml.getBytes(UTF_8);
	}

	@Override
	public void deserialize(byte[] content, Object object)
	{
		if (content != null)
		{
			String xml = new String(content, UTF_8);
			_xstream.fromXML(xml, object);
		}
	}

	final private XStream _xstream = new XStream()
	{
		// http://kenai.com/jira/browse/GUTS-49
		@Override protected MapperWrapper wrapMapper(MapperWrapper next)
		{
			return new MapperWrapper(next)
			{
				@SuppressWarnings("rawtypes") @Override 
				public boolean shouldSerializeMember(Class definedIn, String fieldName)
				{
					try
					{
						return definedIn != Object.class || realClass(fieldName) != null;
					}
					catch (CannotResolveClassException e)
					{
						return false;
					}
				}
			};
		}
	};
}
