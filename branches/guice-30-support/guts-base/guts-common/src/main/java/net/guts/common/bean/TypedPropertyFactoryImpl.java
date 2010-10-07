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

package net.guts.common.bean;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class TypedPropertyFactoryImpl implements TypedPropertyFactory
{
	@Inject TypedPropertyFactoryImpl(UntypedPropertyFactory factory)
	{
		_factory = factory;
	}
	
	@Override public <T> TypedProperty<T, ?> property(String name, Class<T> bean)
	{
		UntypedProperty property = _factory.property(name, bean);
		if (property != null)
		{
			return new TypedProperty<T, Object>(property);
		}
		else
		{
			return null;
		}
	}

	@Override 
	public <T, V> TypedProperty<T, V> property(String name, Class<T> bean, Class<V> type)
	{
		UntypedProperty property = _factory.property(name, bean, type);
		if (property != null)
		{
			return new TypedProperty<T, V>(property);
		}
		else
		{
			return null;
		}
	}

	final private UntypedPropertyFactory _factory;
}
