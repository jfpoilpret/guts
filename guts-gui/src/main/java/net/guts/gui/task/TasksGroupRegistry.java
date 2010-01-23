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

package net.guts.gui.task;

import java.util.Locale;

import net.guts.common.cleaner.Cleaner;
import net.guts.common.ref.WeakRefSet;
import net.guts.common.ref.WeakRefSet.Performer;
import net.guts.event.Consumes;
import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class TasksGroupRegistry
{
	@Inject TasksGroupRegistry(ResourceInjector injector, Cleaner cleaner)
	{
		_injector = injector;
		cleaner.addCleanable(_groups);
	}
	
	public void registerTasksGroup(TasksGroup group)
	{
		// Inject resources into group
		if (_groups.add(group))
		{
			//TODO do we need to inject resources into TasksGroup????
			// Inject resources into group
			_injector.injectInstance(group, group.name());
		}
	}
	
	@Consumes public void localeChanged(Locale locale)
	{
		_groups.perform(new Performer<TasksGroup>()
		{
			@Override public boolean perform(TasksGroup group)
			{
				_injector.injectInstance(group, group.name());
				return true;
			}
		});
	}

	final private WeakRefSet<TasksGroup> _groups = WeakRefSet.create();
	final private ResourceInjector _injector;
}
