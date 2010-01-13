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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.guts.common.cleaner.Cleanable;
import net.guts.common.cleaner.Cleaner;
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
		cleaner.addCleanable(new Cleanable()
		{
			@Override public void cleanup()
			{
				TasksGroupRegistry.this.cleanup();
			}
		});
	}
	
	public void registerTasksGroup(TasksGroup group)
	{
		// Inject resources into group
		_injector.injectInstance(group, group.name());
		synchronized (_groups)
		{
			_groups.add(new WeakReference<TasksGroup>(group));
		}
	}
	
	@Consumes public void localeChanged(Locale locale)
	{
		boolean cleanup = false;
		synchronized (_groups)
		{
			for (WeakReference<TasksGroup> groupRef: _groups)
			{
				TasksGroup group = groupRef.get();
				if (group != null)
				{
					_injector.injectInstance(group, group.name());
				}
				else
				{
					cleanup = true;
				}
			}
		}
		if (cleanup)
		{
			cleanup();
		}
	}

	private void cleanup()
	{
		synchronized (_groups)
		{
			Iterator<WeakReference<TasksGroup>> i = _groups.iterator();
			while (i.hasNext())
			{
				if (i.next().get() == null)
				{
					i.remove();
				}
			}
		}
	}
	
	final private List<WeakReference<TasksGroup>> _groups = 
		new ArrayList<WeakReference<TasksGroup>>();
	final private ResourceInjector _injector;
}
