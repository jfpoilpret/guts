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

 import java.util.concurrent.ExecutorService;

import net.guts.gui.task.Task;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.blocker.InputBlocker;

/**
 * An abstract {@link GutsAction} including support for launching one long task in
 * background, based on Guts-GUI {@link net.guts.gui.task} package.
 * <p/>
 * This class is provided as a convenience as it reduces boiler-plate code
 * needed to inject {@link net.guts.gui.task.TasksGroupFactory} into {@code GutsAction} 
 * subclasses.
 *
 * @author Jean-Francois Poilpret
 * @see Task
 * @see TasksGroupAction
 */
// CSOFF: AbstractClassNameCheck
abstract public class TaskAction extends TasksGroupAction
{
	/**
	 * Create a new {@code TaskAction} and assign it a {@code name} that 
	 * will be used as a key for resource injection.
	 * 
	 * @param name the name used as a key for resource injection
	 */
	protected TaskAction(String name)
	{
		this(name, false);
	}

	/**
	 * Create a new {@code TaskAction} without a name. A name will be automatically
	 * set by Guts-GUI before injecting resources into it, according to the naming
	 * policy defined by a Guice binding for {@link ActionNamePolicy}.
	 */
	protected TaskAction()
	{
		this(null, false);
	}

	/**
	 * Create a new {@code TaskAction} and assign it a {@code name} that 
	 * will be used as a key for resource injection.
	 * 
	 * @param name the name used as a key for resource injection
	 * @param cancellable indicates whether {@link #submit(Task)} will create a 
	 * cancelable {@code TasksGroup} for {@code Task} execution
	 * be canceled by the end user
	 */
	protected TaskAction(String name, boolean cancellable)
	{
		super(name);
		_cancellable = cancellable;
	}

	/**
	 * Immediately start background execution of the given {@code task}, using the
	 * default {@link ExecutorService} (as defined by 
	 * {@link net.guts.gui.task.Tasks#bindDefaultExecutorService}) and no 
	 * {@link InputBlocker}, meaning that user input won't be blocked during execution 
	 * of {@code task}.
	 * <p/>
	 * The method returns immediately without waiting for {@code task} to be finished.
	 * 
	 * @param task the task to execute in background
	 */
	final protected void submit(Task<?> task)
	{
		submit(task, null, null);
	}

	/**
	 * Immediately start background execution of the given {@code task}, using the
	 * default {@link ExecutorService} (as defined by 
	 * {@link net.guts.gui.task.Tasks#bindDefaultExecutorService}) and the provided 
	 * {@link InputBlocker}.
	 * <p/>
	 * The method returns immediately without waiting for {@code task} to be finished.
	 * 
	 * @param task the task to execute in background
	 * @param blocker an {@code InputBlocker} implementation that will be in charge
	 * of blocking the input during the complete execution of {@code task}
	 */
	final protected void submit(Task<?> task, InputBlocker blocker)
	{
		submit(task, blocker, null);
	}
	
	/**
	 * Immediately start background execution of the given {@code task}, using the
	 * provided {@link ExecutorService} and {@link InputBlocker}.
	 * <p/>
	 * The method returns immediately without waiting for {@code task} to be finished.
	 * 
	 * @param task the task to execute in background
	 * @param blocker an {@code InputBlocker} implementation that will be in charge
	 * of blocking the input during the complete execution of {@code task}
	 * @param executor the service in charge of executing {@code task}
	 */
	final protected void submit(Task<?> task, InputBlocker blocker, ExecutorService executor)
	{
		if (task != null)
		{
			TasksGroup group = newTasksGroup(name(), _cancellable, executor, blocker);
			group.add(task);
			group.execute();
		}
	}
	
	final private boolean _cancellable;
}
//CSON: AbstractClassNameCheck
