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

import net.guts.gui.task.blocker.InputBlocker;

/**
 * Factory of {@link TasksGroup}s. This service is the only way to create 
 * {@code TasksGroup} instances.
 * <p/>
 * This factory is bound with Guice and must thus be injected into your own
 * classes that need to create {@code TasksGroup}s.
 *
 * @author Jean-Francois Poilpret
 */
public interface TasksGroupFactory
{
	/**
	 * Create a new {@code TasksGroup}, ready to be added {@link Task}s and then
	 * executed.
	 * 
	 * @param name the name to be given to the new {@code TasksGroup}, can be used 
	 * for identifying a group of running {@code Task}s in a status bar for instance.
	 * @param cancellable make the new {@code TasksGroup} cancellable or not;
	 * once a {@code TasksGroup} has been constructed, this property cannot be
	 * changed.
	 * @param executor the executor service that will be used to run all 
	 * {@code Task}s of the new {@code TasksGroup}; if {@code null}, then the
	 * default {@code ExecutorService}, as defined by 
	 * {@link Tasks#bindDefaultExecutorService}; if no default has been defined,
	 * then {@link java.util.concurrent.ThreadPoolExecutor} is used.
	 * @param blocker the {@code InputBlocker} that will be used to block user
	 * input during execution of this {@code TasksGroup}; {@code blocker} will
	 * be automatically injected (fields and methods, not constructor) by Guice,
	 * and also resource injected (by {@link net.guts.gui.resource.ResourceInjector}).
	 * @return a new {@code TasksGroup}
	 */
	public TasksGroup newTasksGroup(
		String name, boolean cancellable, ExecutorService executor, InputBlocker blocker);
}
