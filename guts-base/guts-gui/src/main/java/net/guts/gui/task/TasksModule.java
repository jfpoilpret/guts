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

import net.guts.common.injection.AbstractSingletonModule;
import net.guts.common.injection.InjectionListeners;
import net.guts.common.injection.Matchers;
import net.guts.common.injection.OneTypeListener;
import net.guts.gui.resource.ResourceModule;

import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Guice {@link com.google.inject.Module} for Guts-GUI Tasks Management system.
 * This module must be added to the list of modules passed to 
 * {@link com.google.inject.Guice#createInjector}:
 * <pre>
 * Injector injector = Guice.createInjector(new TasksModule(), ...);
 * </pre>
 * If you use Guts-GUI {@link net.guts.gui.application.AbstractApplication}, then
 * {@code TasksModule} is automatically added to the list of {@code Module}s used
 * by Guts-GUI to create Guice {@code Injector}.
 * <p/>
 * Hence you would care about {@code TasksModule} only if you intend to use 
 * Guts-GUI Tasks Management system but don't want to use the whole 
 * Guts-GUI framework.
 *
 * @author Jean-Francois Poilpret
 */
public final class TasksModule extends AbstractSingletonModule
{
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override protected void configure()
	{
		// Make sure Resource Injection system is installed
		install(new ResourceModule());

		// Bind TasksGroupFactory
		install(new FactoryModuleBuilder()
			.implement(TasksGroup.class, TasksGroup.class)
			.build(TasksGroupFactory.class));

		// Add type listener to automatically register new TasksGroup
		TasksGroupRegisterInjectionListener injectionListener = 
			InjectionListeners.requestInjection(
				binder(), new TasksGroupRegisterInjectionListener());
		OneTypeListener<TasksGroup> typeListener = 
			new OneTypeListener<TasksGroup>(TasksGroup.class, injectionListener);
		bindListener(Matchers.isSubtypeOf(TasksGroup.class), typeListener);
	}
}
