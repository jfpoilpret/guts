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

// CSOFF: AbstractClassNameCheck
abstract public class TaskAction extends TasksGroupAction
{
	protected TaskAction(String name)
	{
		this(name, false);
	}

	protected TaskAction(String name, boolean cancellable)
	{
		super(name);
		_cancellable = cancellable;
	}

	final protected void submit(Task<?> task)
	{
		submit(task, null, null);
	}
	
	final protected void submit(Task<?> task, InputBlocker blocker)
	{
		submit(task, blocker, null);
	}
	
	final protected void submit(Task<?> task, InputBlocker blocker, ExecutorService executor)
	{
		if (task != null)
		{
			TasksGroup group = tasksGroupFactory().newTasksGroup(name(), _cancellable);
			group.add(task);
			group.getExecutor(executor, blocker).execute();
		}
	}
	
	final private boolean _cancellable;
}
//CSON: AbstractClassNameCheck
