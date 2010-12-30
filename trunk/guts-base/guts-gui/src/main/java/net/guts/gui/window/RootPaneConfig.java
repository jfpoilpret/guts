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

package net.guts.gui.window;

import javax.swing.RootPaneContainer;

import net.guts.gui.util.TypeSafeMap;

public class RootPaneConfig<T extends RootPaneContainer>
{
	RootPaneConfig(TypeSafeMap properties)
	{
		_properties = properties;
	}
	
	public <V> V get(Class<V> type)
	{
		return _properties.get(type);
	}

	public <V> V get(String key, Class<V> type)
	{
		return _properties.get(key, type);
	}
	
	final private TypeSafeMap _properties;
}
