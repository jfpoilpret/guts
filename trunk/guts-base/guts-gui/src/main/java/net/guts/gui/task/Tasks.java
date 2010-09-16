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

import java.util.concurrent.ExecutorService;

import com.google.inject.Binder;
import com.google.inject.binder.LinkedBindingBuilder;

/**
 * Utility class to define, from within a Guice {@link com.google.inject.Module}, the binding
 * of the default {@link ExecutorService} to be used by {@link TasksGroup}s created by
 * {@link TasksGroupFactory#newTasksGroup} when its {@code executor} argument is {@code null}.
 *
 * @author Jean-Francois Poilpret
 */
final public class Tasks
{
	private Tasks()
	{
	}

	/**
	 * Initializes the binding for the default {@link ExecutorService} to be used by
	 * {@link TasksGroup} when {@code executor} is {@code null} in calls to
	 * {@code TasksGroupFactory#newTasksGroup(name, cancellable, executor, blocker)}.
	 * <p/>
	 * This is based on usual Guice EDSL for bindings:
	 * <pre>
	 * Tasks.bindDefaultExecutorService(binder()).toInstance(Executors.newFixedThreadPool(10));
	 * </pre>
	 * This must be called from {@link com.google.inject.Module#configure(Binder)}.
	 * 
	 * @param binder the Guice binder passed to 
	 * {@link com.google.inject.Module#configure(Binder)}
	 * @return a {@link com.google.inject.binder.LinkedBindingBuilder} to bind to an 
	 * {@link ExecutorService}
	 */
	static public LinkedBindingBuilder<ExecutorService> bindDefaultExecutorService(
		Binder binder)
	{
		return binder.bind(ExecutorService.class).annotatedWith(DefaultExecutor.class);
	}
}
