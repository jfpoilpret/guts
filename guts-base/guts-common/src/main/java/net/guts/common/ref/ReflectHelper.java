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

package net.guts.common.ref;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class ReflectHelper
{
	static final private Logger _logger = LoggerFactory.getLogger(ReflectHelper.class);

	private ReflectHelper()
	{
	}
	
	static public interface FieldValueProcessor<T>
	{
		public boolean process(Field field, T value);
	}
	
	static public List<Field> findFields(Class<?> parent, Class<?> fieldType)
	{
		List<Field> fields = new ArrayList<Field>();
		Class<?> clazz = parent;
		while (clazz != null)
		{
			for (Field field: clazz.getDeclaredFields())
			{
				if (fieldType.isAssignableFrom(field.getType()))
				{
					fields.add(field);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return fields;
	}

	// CSOFF: IllegalCatchCheck
	@SuppressWarnings("unchecked") 
	static public <T> T getFieldValue(Object parent, Field field, Class<T> fieldType)
	{
		boolean accessible = field.isAccessible();
		try
		{
			field.setAccessible(true);
			T value =  (T) field.get(parent);
			return value;
		}
		catch (Exception e)
		{
			String name = field.getDeclaringClass().getName() + "." + field.getName();
			_logger.error("getFieldValue(), field: " + name, e);
			return null;
		}
		finally
		{
			field.setAccessible(accessible);
		}
	}
	// CSON: IllegalCatchCheck
	
	static public <T> void processFieldsValues(Object parent, List<Field> fields, 
		Class<T> fieldType, FieldValueProcessor<T> processor)
	{
		for (Field field: fields)
		{
			T value = getFieldValue(parent, field, fieldType);
			if (value != null)
			{
				if (!processor.process(field, value))
				{
					return;
				}
			}
		}
	}
	
	static public <T> void processFieldsValues(
		Object parent, Class<T> fieldType, FieldValueProcessor<T> processor)
	{
		List<Field> fields = findFields(parent.getClass(), fieldType);
		processFieldsValues(parent, fields, fieldType, processor);
	}
	
	static public <T> List<T> findFieldsValues(Object parent, Class<T> fieldType)
	{
		return findFieldsValues(
			parent, findFields(parent.getClass(), fieldType), fieldType);
	}

	static public <T> List<T> findFieldsValues(
		Object parent, List<Field> fields, Class<T> fieldType)
	{
		final List<T> values = new ArrayList<T>(fields.size());
		processFieldsValues(parent, fieldType, new FieldValueProcessor<T>()
		{
			@Override public boolean process(Field field, T value)
			{
				values.add(value);
				return true;
			}
		});
		return values;
	}
}
