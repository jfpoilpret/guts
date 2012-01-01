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

import com.google.inject.ImplementedBy;

/**
 * API of the service to serialize and deserialize session state of an
 * application. It is used by {@link SessionManager}.
 * <p/>
 * The serialization contract provided by this service is as follows:
 * All non {@code static} and non {@code final} fields, whatever their 
 * access (including {@code private}), are serialized, except if they are 
 * declared {@code transient}.
 * <p/>
 * Default implementation uses <a href="http://xstream.codehaus.org/">XStream</a>.
 * <p/>
 * You normally won't need to use or override this service, except if you're not
 * satisfied with having your application depend on XStream.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(XStreamSerializationManager.class)
public interface SerializationManager
{
	/**
	 * Serializes {@code object} into binary format, ready for storage.
	 * 
	 * @param object the object to serialize
	 * @return the serialized form of {@code object}
	 */
	public byte[] serialize(Object object);

	/**
	 * Deserializes {@code content} (binary format, result of a previous 
	 * serialization by {@link #serialize}) into {@code object}. {@code object}
	 * must have been instantiated first; its fields will be overridden.
	 * <p/>
	 * If {@code content} is not in a suitable format and cannot be used to
	 * populate {@code object} fields, then {@code object} will not be modified;
	 * no error is notified in this case.
	 * 
	 * @param content binary format to be used for {@code object} deserialization
	 * @param object the object to be restored (its fields) from binary 
	 * {@code content}
	 */
	public void deserialize(byte[] content, Object object);
}
