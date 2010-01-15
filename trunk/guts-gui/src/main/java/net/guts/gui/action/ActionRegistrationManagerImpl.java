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

package net.guts.gui.action;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.cleaner.Cleaner;
import net.guts.common.ref.WeakRefSet;
import net.guts.common.ref.WeakRefSet.Performer;
import net.guts.event.Consumes;
import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
class ActionRegistrationManagerImpl implements ActionRegistrationManager
{
	static final private Logger _logger = 
		LoggerFactory.getLogger(ActionRegistrationManagerImpl.class);
	
	@Inject ActionRegistrationManagerImpl(Injector injector, 
		ResourceInjector resourceInjector, Cleaner cleaner)
	{
		_injector = injector;
		_resourceInjector = resourceInjector;
		cleaner.addCleanable(_actions);
	}

	// CSOFF: IllegalCatchCheck
	@Override public void registerActions(Object instance)
	{
		// Check all fields of instance class
		for (Field field: findActions(instance.getClass()))
		{
			boolean accessible = field.isAccessible();
			try
			{
				field.setAccessible(true);
				GutsAction action =  (GutsAction) field.get(instance);
				registerAction(action);
			}
			catch (Exception e)
			{
				String name = field.getDeclaringClass().getName() + "." + field.getName();
				_logger.error("registerActions(), field: " + name, e);
			}
			finally
			{
				field.setAccessible(accessible);
			}
		}
	}
	// CSON: IllegalCatchCheck
	
	@Override public void registerAction(GutsAction action)
	{
		if (_actions.add(action))
		{
			if (!action.isMarkedInjected())
			{
				_injector.injectMembers(action);
			}
			_resourceInjector.injectInstance(action, action.name());
		}
	}
	
	@Consumes(priority = Integer.MIN_VALUE + 2) 
	public void localeChanged(Locale locale)
	{
		_actions.perform(new Performer<GutsAction>()
		{
			@Override public boolean perform(GutsAction action)
			{
				_resourceInjector.injectInstance(action, action.name());
				return true;
			}
		});
	}
	
	private List<Field> findActions(Class<?> type)
	{
		synchronized (_actionClasses)
		{
			List<Field> fields = _actionClasses.get(type);
			if (fields == null)
			{
				fields = new ArrayList<Field>();
				Class<?> clazz = type;
				while (clazz != null)
				{
					for (Field field: clazz.getDeclaredFields())
					{
						if (GutsAction.class.isAssignableFrom(field.getType()))
						{
							fields.add(field);
						}
					}
					clazz = clazz.getSuperclass();
				}
				_actionClasses.put(type, fields);
			}
			return fields;
		}
	}
	
	final private Map<Class<?>, List<Field>> _actionClasses = 
		new HashMap<Class<?>, List<Field>>();
	final private WeakRefSet<GutsAction> _actions = WeakRefSet.create();
	final private ResourceInjector _resourceInjector;
	final private Injector _injector;
}
