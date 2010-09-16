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

package net.guts.common.log;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.injection.Matchers;

import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public final class LogModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		bindListener(Matchers.hasFieldsOfType(Logger.class), new SLF4JTypeListener());
	}
}

class SLF4JTypeListener implements TypeListener
{
	public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter)
	{
		for (Field field: typeLiteral.getRawType().getDeclaredFields())
		{
			if (	field.getType() == Logger.class
				&&	field.isAnnotationPresent(InjectLogger.class))
			{
				typeEncounter.register(new SLF4JMembersInjector<T>(field));
			}
		}
	}
}

class SLF4JMembersInjector<T> implements MembersInjector<T>
{
	SLF4JMembersInjector(Field field)
	{
		_field = field;
		_logger = LoggerFactory.getLogger(_field.getDeclaringClass());
		_field.setAccessible(true);
	}

	public void injectMembers(T t)
	{
		try
		{
			_field.set(t, _logger);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	final private Field _field;
	final private Logger _logger;
}
