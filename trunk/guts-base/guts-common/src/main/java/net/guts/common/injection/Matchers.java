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

package net.guts.common.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.guts.common.type.TypeHelper;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

public final class Matchers
{
	private Matchers()
	{
	}

	static final public Matcher<? super Class<?>> anyClass()
	{
		return ANY;
	}
	
	static final private Matcher<? super Class<?>> ANY = new AbstractMatcher<Class<?>>()
	{
		@Override public boolean matches(Class<?> type)
		{
			return true;
		}
	};
	
	static final public Matcher<TypeLiteral<?>> hasFieldsOfType(final Class<?>... fieldTypes)
	{
		return new AbstractMatcher<TypeLiteral<?>>()
		{
			@Override public boolean matches(TypeLiteral<?> type)
			{
				Class<?> clazz = type.getRawType();
				while (clazz != null)
				{
					for (Field field: clazz.getDeclaredFields())
					{
						for (Class<?> fieldType: fieldTypes)
						{
							if (fieldType.isAssignableFrom(field.getType()))
							{
								return true;
							}
						}
					}
					clazz = clazz.getSuperclass();
				}
				return false;
			}
		};
	}
	
	static final public Matcher<TypeLiteral<?>> isSubtypeOf(final Class<?> supertype)
	{
		return isSubtypeOf(TypeLiteral.get(supertype));
	}
	
	static final public Matcher<TypeLiteral<?>> isSubtypeOf(final TypeLiteral<?> supertype)
	{
		return new AbstractMatcher<TypeLiteral<?>>()
		{
			@Override public boolean matches(TypeLiteral<?> type)
			{
				boolean match = TypeHelper.typeIsSubtypeOf(type, supertype);
				return match;
			}
		};
	}
	
	static final public Matcher<TypeLiteral<?>> hasMethodAnnotatedWith(
		final Class<? extends Annotation> annotation)
	{
		return new AbstractMatcher<TypeLiteral<?>>()
		{
			@Override public boolean matches(TypeLiteral<?> type)
			{
				Class<?> clazz = type.getRawType();
				while (clazz != null)
				{
					for (Method m: clazz.getDeclaredMethods())
					{
						if (m.isAnnotationPresent(annotation))
						{
							return true;
						}
					}
					clazz = clazz.getSuperclass();
				}
				return false;
			}
		};
	}
	
	static final public Matcher<TypeLiteral<?>> isAnnotatedWith(
		final Class<? extends Annotation> annotation)
	{
		return new AbstractMatcher<TypeLiteral<?>>()
		{
			@Override public boolean matches(TypeLiteral<?> type)
			{
				Class<?> clazz = type.getRawType();
				return clazz.isAnnotationPresent(annotation);
			}
		};
	}
	
	static final public Matcher<TypeLiteral<?>> hasPublicMethodAnnotatedWith(
		final Class<? extends Annotation> annotation)
	{
		return new AbstractMatcher<TypeLiteral<?>>()
		{
			@Override public boolean matches(TypeLiteral<?> type)
			{
				for (Method m: type.getRawType().getMethods())
				{
					if (m.isAnnotationPresent(annotation))
					{
						return true;
					}
				}
				return false;
			}
		};
	}
	
	
	static final public Matcher<? super Method> isMethodReturnSubtype(final Class<?> type)
	{
		return new AbstractMatcher<Method>()
		{
			@Override public boolean matches(Method method)
			{
				return type.isAssignableFrom(method.getReturnType());
			}
		};
	}
	
	static final public Matcher<? super Method> isMethodReturnType(final TypeLiteral<?> type)
	{
		return new AbstractMatcher<Method>()
		{
			@Override public boolean matches(Method method)
			{
				return method.getGenericReturnType() == type.getType();
			}
		};
	}
	
	static final public Matcher<? super Method> isMethodReturnType(Class<?> type)
	{
		return isMethodReturnType(TypeLiteral.get(type));
	}
}
