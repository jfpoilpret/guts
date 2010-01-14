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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import net.guts.gui.task.blocker.InputBlocker;
import net.guts.gui.task.blocker.InputBlockers;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

public class TasksGroup
{
	@Inject TasksGroup(ExecutorServiceRegistry executorRegistry, Injector injector,
		DefaultExecutorHolder holder, @Assisted String name, @Assisted boolean cancellable)
	{
		_defaultExecutor = holder._executor;
		_executorRegistry = executorRegistry;
		_injector = injector;
		_name = name;
		_cancellable = cancellable;
		_executor = new TasksGroupExecutor(this);
	}
	
	public boolean isCancellable()
	{
		return _cancellable;
	}
	
	public <T> TasksGroup add(Task<T> task, TaskListener<T> listener)
	{
		checkMutability("add Task");
		TaskHandler<T> handler = new TaskHandler<T>(this, task, listener);
		// Add all general listeners (already registered) to the new TaskHandler
		// But the problem is that we don't store them anywhere!
		for (TaskListener<Object> taskListener: _tasksListeners)
		{
			handler.addListener(taskListener);
		}
		_tasks.add(handler);
		fireTaskAdded(task);
		_executor.addTaskHandler(handler);
		return this;
	}
	
	public <T> TasksGroup add(Task<T> task)
	{
		if (task instanceof AbstractTask)
		{
			return add(task, (AbstractTask<T>) task);
		}
		else
		{
			return add(task, null);
		}
	}
	
	public void addListener(TaskListener<Object> listener)
	{
		if (listener != null)
		{
			checkMutability("add TaskListener");
			_tasksListeners.add(listener);
			for (TaskHandler<?> handler: _tasks)
			{
				handler.addListener(listener);
			}
		}
	}
	
	public void addGroupListener(TasksGroupListener listener)
	{
		checkMutability("add TasksGroupListener");
		_groupListeners.add(listener);
	}

	//TODO shouldn't we impose executor and blocker as ctor arguments????
	public TasksGroupExecutor getExecutor(ExecutorService executor, InputBlocker blocker)
	{
		// Make sure all arguments are suitable, change them to defaults otherwise
		return initExecutor((executor != null ? executor : _defaultExecutor), 
			(blocker != null ? blocker : InputBlockers.noBlocker()));
	}
	
	public TasksGroupExecutor getExecutor(InputBlocker blocker)
	{
		return getExecutor(null, blocker);
	}
	
	public TasksGroupExecutor getExecutor()
	{
		return getExecutor(null, null);
	}
	
	private TasksGroupExecutor initExecutor(ExecutorService executor, InputBlocker blocker)
	{
		if (_executorInitialized.compareAndSet(false, true))
		{
			// Make sure InputBlocker is injected (we can't make sure of that fact before)
			_injector.injectMembers(blocker);
			_executor.init(blocker, executor);
			// Register executor with ExecutorServiceRegistry (for proper shutdown)
			_executorRegistry.registerExecutor(executor);
		}
		return _executor;
	}
	
	String name()
	{
		return _name;
	}
	
	List<TaskHandler<?>> tasks()
	{
		return _tasks;
	}

	private void checkMutability(String operation)
	{
		if (_executor.isCancelled())
		{
			throw new IllegalStateException(
				"Can't " + operation + " to a cancelled TasksGroup!");
		}
	}
	
	void fireTaskAdded(Task<?> task)
	{
		for (TasksGroupListener groupListener: _groupListeners)
		{
			groupListener.taskAdded(this, task);
		}
	}

	void fireTaskStarted(Task<?> task)
	{
		for (TasksGroupListener groupListener: _groupListeners)
		{
			groupListener.taskStarted(this, task);
		}
	}

	void fireTaskEnded(Task<?> task)
	{
		for (TasksGroupListener groupListener: _groupListeners)
		{
			groupListener.taskEnded(this, task);
		}
	}

	void fireAllTasksEnded()
	{
		for (TasksGroupListener groupListener: _groupListeners)
		{
			groupListener.allTasksEnded(this);
		}
	}

	// Workaround to Guice limitation on injection of optional args to constructors
	// See http://code.google.com/p/google-guice/wiki/FrequentlyAskedQuestions
	// "How can I inject optional parameters into a constructor?"
	//CSOFF: VisibilityModifierCheck
	static class DefaultExecutorHolder
	{
		@Inject(optional = true) @DefaultExecutor ExecutorService _executor =
			Executors.newCachedThreadPool();
	}
	//CSON: VisibilityModifierCheck

	final private ExecutorService _defaultExecutor;
	final private ExecutorServiceRegistry _executorRegistry;
	final private Injector _injector;
	final private String _name;
	final private boolean _cancellable;
	final private List<TaskHandler<?>> _tasks = new CopyOnWriteArrayList<TaskHandler<?>>();
	final private List<TaskListener<Object>> _tasksListeners = 
		new CopyOnWriteArrayList<TaskListener<Object>>();
	final private List<TasksGroupListener> _groupListeners = 
		new CopyOnWriteArrayList<TasksGroupListener>();
	final private TasksGroupExecutor _executor;
	final private AtomicBoolean _executorInitialized = new AtomicBoolean();
}
