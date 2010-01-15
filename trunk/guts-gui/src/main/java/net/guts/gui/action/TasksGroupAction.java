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
import net.guts.gui.task.TasksGroupFactory;
import net.guts.gui.task.blocker.InputBlocker;

import com.google.inject.Inject;

// CSOFF: AbstractClassNameCheck
abstract public class TasksGroupAction extends GutsAction
{
	protected TasksGroupAction(String name)
	{
		super(name);
	}

	final protected TasksGroup newTasksGroup(
		String name, boolean cancellable, ExecutorService executable, InputBlocker blocker)
	{
		return _factory.newTasksGroup(name, cancellable, executable, blocker);
	}

	final protected TasksGroup newTasksGroup(
		String name, boolean cancellable, ExecutorService executable)
	{
		return _factory.newTasksGroup(name, cancellable, executable, null);
	}

	final protected TasksGroup newTasksGroup(
		String name, boolean cancellable, InputBlocker blocker)
	{
		return _factory.newTasksGroup(name, cancellable, null, blocker);
	}

	final protected TasksGroup newTasksGroup(String name, boolean cancellable)
	{
		return _factory.newTasksGroup(name, cancellable, null, null);
	}

	@Inject void init(TasksGroupFactory factory)
	{
		_factory = factory;
	}

	private TasksGroupFactory _factory;
}
//CSON: AbstractClassNameCheck
