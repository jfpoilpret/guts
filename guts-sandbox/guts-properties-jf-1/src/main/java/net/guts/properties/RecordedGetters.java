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

package net.guts.properties;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

//TODO refactor storage of called getters into a static util class, shared by all mocks
//TODO then push each descriptors to a stack in a ThreadLocal
final class RecordedGetters
{
	private RecordedGetters()
	{
	}
	
	static List<PropertyDescriptor> calledProperties()
	{
		List<PropertyDescriptor> properties = _calledProperties.get();
		_calledProperties.set(new ArrayList<PropertyDescriptor>(0));
		return properties;
	}
	
	static void pushProperty(PropertyDescriptor property)
	{
		_calledProperties.get().add(property);
	}
	
	static private final ThreadLocal<List<PropertyDescriptor>> _calledProperties = 
		new ThreadLocal<List<PropertyDescriptor>>()
	{
		@Override protected List<PropertyDescriptor> initialValue()
		{
			return new ArrayList<PropertyDescriptor>(0);
		}
	};
}
