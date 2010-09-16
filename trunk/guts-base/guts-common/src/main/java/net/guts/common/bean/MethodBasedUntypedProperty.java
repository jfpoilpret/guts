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

import java.lang.reflect.Method;

import com.google.inject.TypeLiteral;

class MethodBasedUntypedProperty extends UntypedProperty
{
	MethodBasedUntypedProperty(String name, TypeLiteral<?> type, Method setter, Method getter)
	{
		super(name, type, setter, getter);
		_setter = setter;
		_getter = getter;
	}

	@Override void setValue(Object bean, Object value) throws Exception
	{
		_setter.invoke(bean, value);
	}
	
	@Override Object getValue(Object bean) throws Exception
	{
		return _getter.invoke(bean);
	}

	final private Method _setter;
	final private Method _getter;
}
