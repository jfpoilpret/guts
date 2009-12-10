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
 * API of the service to physically store session state from an application. 
 * It is used by {@link SessionManager}.
 * <p/>
 * Default implementation uses {@link java.util.prefs.Preferences} as actual
 * storage.
 * <p/>
 * You normally won't need to use this service but you may want to override the
 * default implementation, for instance you could provide an implementation
 * that stores data in a file, which could be useful for debugging or automatic
 * tests of your application.
 * <p/>
 * Overriding default implementation must be done in one of your 
 * {@link com.google.inject.Module}s:
 * <pre>
 * bind(StorageMedium.class).to(MyFileStorageMedium.class);
 * </pre>
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(PrefsStorageMedium.class)
public interface StorageMedium
{
	/**
	 * Save binary {@code content} under the name {@code name}.
	 * 
	 * @param name name to be used for storing {@code content}
	 * @param content binary content to be stored
	 */
	public void save(String name, byte[] content);
	
	/**
	 * Load binary content from storage named {@code name}.
	 * 
	 * @param name name under which content was previously saved
	 * @return binary content with name {@code name}, or {@code null} if no
	 * storage can be found with this {@code name}
	 */
	public byte[] load(String name);
}
