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

import java.util.Set;

import com.google.inject.TypeLiteral;

// NB: useful only for ComponentInjector implementers (for extensibility purposes) 
public interface ResourceMap
{
	// Returns all resource keys starting with prefix. Prefix has been removed from all keys
	// in the returned set (for ease of use)
	public Set<Key> keys(String prefix);
	public <T> T getValue(Key key, Class<T> type);
	public <T> T getValue(Key key, TypeLiteral<T> type);
	
	static public interface Key
	{
		public String key();
		public String prefix();
	}
}
