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

import net.guts.common.injection.InjectionListeners;
import net.guts.common.injection.Matchers;
import net.guts.common.injection.OneTypeListener;
import net.guts.gui.action.blocker.BlockerModule;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public final class ActionModule extends AbstractModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Make sure Resource Injection system is installed
		install(new ResourceModule());
		// Install other specific dependencies
		install(new BlockerModule());
		// Add special Injector for GutsAction
		Resources.bindInstanceInjector(binder(), GutsAction.class)
			.to(GutsActionInjector.class).asEagerSingleton();

		// Create default TaskService
		Actions.bindTaskService(binder(), Actions.DEFAULT_TASK_SERVICE)
			.to(DefaultTaskService.class).in(Scopes.SINGLETON);
		// Add type listener to automatically register Action fields 
		// of Guice-instantiated objects
		ActionRegisterInjectionListener injectionListener = 
			InjectionListeners.requestInjection(
				binder(), new ActionRegisterInjectionListener());
		OneTypeListener<Object> typeListener = 
			new OneTypeListener<Object>(Object.class, injectionListener);
		bindListener(Matchers.hasFieldsOfType(GutsAction.class), typeListener);
	}

	@Override public boolean equals(Object other)
	{
		return other instanceof ActionModule;
	}

	@Override public int hashCode()
	{
		return ActionModule.class.hashCode();
	}
}
