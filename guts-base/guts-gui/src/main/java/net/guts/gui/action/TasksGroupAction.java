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

import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.TasksGroup.Execution;
import net.guts.gui.task.TasksGroupFactory;
import net.guts.gui.task.blocker.InputBlocker;

import com.google.inject.Inject;

/**
 * An abstract {@link GutsAction} including support for launching long tasks in
 * background, based on Guts-GUI {@link net.guts.gui.task} package.
 * <p/>
 * This class is provided as a convenience as it reduces boiler-plate code
 * needed to inject {@link TasksGroupFactory} into {@code GutsAction} subclasses.
 * 
 * @author Jean-Francois Poilpret
 * @see TaskAction
 * @see TasksGroup
 */
// CSOFF: AbstractClassNameCheck
abstract public class TasksGroupAction extends GutsAction
{
	/**
	 * Create a new {@code TasksGroupAction} and assign it a {@code name} that 
	 * will be used as a key for resource injection.
	 * 
	 * @param name the name used as a key for resource injection
	 */
	protected TasksGroupAction(String name)
	{
		super(name);
	}

	/**
	 * Create a new {@code TasksGroupAction} without a name. A name will be automatically
	 * set by Guts-GUI before injecting resources into it, according to the naming
	 * policy defined by a Guice binding for {@link ActionNamePolicy}.
	 */
	protected TasksGroupAction()
	{
		super();
	}

	/**
	 * Create a new {@code TasksGroup} that can be used to set up and launch one
	 * or more long {@link net.guts.gui.task.Task}s in background.
	 * This method is exactly the same as {@link TasksGroupFactory#newTasksGroup}.
	 * 
	 * @param name the name to be given to the new {@code TasksGroup}, used as a key
	 * for resource injection
	 * @param execution indicates whether the new {@code TasksGroup} can potentially
	 * be canceled by the end user
	 * @param executor the service in charge of executing all {@code Task}s added to
	 * the new {@code TasksGroup}
	 * @param blocker an {@code InputBlocker} implementation that will be in charge
	 * of blocking the input during the whole execution of {@code Task}s added to
	 * the new {@code TasksGroup}
	 * @return a new {@code TasksGroup} ready to be added {@code Task}s and executed
	 */
	final protected TasksGroup newTasksGroup(
		String name, Execution execution, ExecutorService executor, InputBlocker blocker)
	{
		return _factory.newTasksGroup(name, execution, executor, blocker);
	}

	/**
	 * Create a new {@code TasksGroup} that can be used to set up and launch one
	 * or more long {@link net.guts.gui.task.Task}s in background.
	 * The created {@code TasksGroup} has no {@link InputBlocker}, meaning that
	 * user input won't be blocked during execution of that {@code TasksGroup}.
	 * 
	 * @param name the name to be given to the new {@code TasksGroup}, used as a key
	 * for resource injection
	 * @param execution indicates whether the new {@code TasksGroup} can potentially
	 * be canceled by the end user
	 * @param executor the service in charge of executing all {@code Task}s added to
	 * the new {@code TasksGroup}
	 * @return a new {@code TasksGroup} ready to be added {@code Task}s and executed
	 */
	final protected TasksGroup newTasksGroup(
		String name, Execution execution, ExecutorService executor)
	{
		return _factory.newTasksGroup(name, execution, executor, null);
	}

	/**
	 * Create a new {@code TasksGroup} that can be used to set up and launch one
	 * or more long {@link net.guts.gui.task.Task}s in background.
	 * The created {@code TasksGroup} will use the default {@link ExecutorService} for
	 * {@code Task}s execution, which can be set with 
	 * {@link net.guts.gui.task.Tasks#bindDefaultExecutorService}.
	 * 
	 * @param name the name to be given to the new {@code TasksGroup}, used as a key
	 * for resource injection
	 * @param execution indicates whether the new {@code TasksGroup} can potentially
	 * be canceled by the end user
	 * @param blocker an {@code InputBlocker} implementation that will be in charge
	 * of blocking the input during the whole execution of {@code Task}s added to
	 * the new {@code TasksGroup}
	 * @return a new {@code TasksGroup} ready to be added {@code Task}s and executed
	 */
	final protected TasksGroup newTasksGroup(
		String name, Execution execution, InputBlocker blocker)
	{
		return _factory.newTasksGroup(name, execution, null, blocker);
	}

	/**
	 * Create a new {@code TasksGroup} that can be used to set up and launch one
	 * or more long {@link net.guts.gui.task.Task}s in background.
	 * The created {@code TasksGroup} has no {@link InputBlocker}, meaning that
	 * user input won't be blocked during execution of that {@code TasksGroup};
	 * also, it will use the default {@link ExecutorService} for
	 * {@code Task}s execution, which can be set with 
	 * {@link net.guts.gui.task.Tasks#bindDefaultExecutorService}.
	 * 
	 * @param name the name to be given to the new {@code TasksGroup}, used as a key
	 * for resource injection
	 * @param execution indicates whether the new {@code TasksGroup} can potentially
	 * be canceled by the end user
	 * @return a new {@code TasksGroup} ready to be added {@code Task}s and executed
	 */
	final protected TasksGroup newTasksGroup(String name, Execution execution)
	{
		return _factory.newTasksGroup(name, execution, null, null);
	}

	@Inject void init(TasksGroupFactory factory)
	{
		_factory = factory;
	}

	private TasksGroupFactory _factory;
}
//CSON: AbstractClassNameCheck
