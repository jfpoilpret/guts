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
 * Interface defining the API to save and load any kinds of preferences to a
 * user-based location.
 * <p/>
 * Default implementation uses {@link java.util.prefs.Preferences}.
 * <p/>
 * Object serialization is based on <a href="http://xstream.codehaus.org/">XStream</a> 
 * library, and hence works as follows:
 * <ul>
 * <li>objects are serialized as an XML stream</li>
 * <li>all no static fields (even private) are serialized</li>
 * <li>objects must have a public default constructor</li>
 * <li>fields that refer to objects are cursively serialized as well</li>
 * <li>{@code transient} fields are not serialized</li>
 * </ul>
 * XStream doesn't require serialized object to implement any interface, use any
 * annotation, or follow JavaBeans conventions (i.e. public getters/setters).
 * <p/>
 * {@code StorageMedium} is used by {@link SessionManager} for physical storage
 * of GUI states, but it can be used for storing other preferences settings.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(PrefsStorageMedium.class)
public interface StorageMedium
{
	/**
	 * Save the fields of {@code content} in a {@link java.util.prefs.Preferences} 
	 * user node, under the key {@code id}. 
	 * 
	 * @param id the key under which to store {@code content}'s fields
	 * @param content the object which fields should be stored
	 */
	public void save(String id, Object content);

	/**
	 * Restore the fields of {@code content} from {@link java.util.prefs.Preferences},
	 * as stored under the given {@code key}.
	 * If no information can currently be found for {@code key}, then {@code content}
	 * doesn't change at all; no exception occurs in this case.
	 * 
	 * @param id the key under which to retrieve {@code content}'s fields
	 * @param content the object which fields should be retrieved
	 */
	public void load(String id, Object content);
}
