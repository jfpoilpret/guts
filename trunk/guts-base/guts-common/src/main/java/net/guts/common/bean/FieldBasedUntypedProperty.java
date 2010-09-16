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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.inject.TypeLiteral;

class FieldBasedUntypedProperty extends UntypedProperty
{
	FieldBasedUntypedProperty(String name, TypeLiteral<?> type, Field field)
	{
		super(name, type, (Modifier.isFinal(field.getModifiers()) ? null : field), field);
		_field = field;
	}
	
	@Override Object getValue(Object bean) throws Exception
	{
		return _field.get(bean);
	}

	@Override void setValue(Object bean, Object value) throws Exception
	{
		_field.set(bean, value);
	}

	private final Field _field;
}
