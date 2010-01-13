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

import net.guts.common.injection.InjectionListeners;
import net.guts.common.injection.Matchers;
import net.guts.common.injection.OneTypeListener;
import net.guts.gui.resource.ResourceModule;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

public final class TasksModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Make sure Resource Injection system is installed
		install(new ResourceModule());

		// Bind TasksGroupFactory
		bind(TasksGroupFactory.class).toProvider(
			FactoryProvider.newFactory(TasksGroupFactory.class, TasksGroup.class));

		// Add type listener to automatically register new TasksGroup
		TasksGroupRegisterInjectionListener injectionListener = 
			InjectionListeners.requestInjection(
				binder(), new TasksGroupRegisterInjectionListener());
		OneTypeListener<TasksGroup> typeListener = 
			new OneTypeListener<TasksGroup>(TasksGroup.class, injectionListener);
		bindListener(Matchers.isSubtypeOf(TasksGroup.class), typeListener);
	}

	@Override public boolean equals(Object other)
	{
		return other instanceof TasksModule;
	}

	@Override public int hashCode()
	{
		return TasksModule.class.hashCode();
	}
}
