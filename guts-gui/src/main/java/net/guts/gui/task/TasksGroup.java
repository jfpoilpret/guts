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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.guts.gui.resource.ResourceInjector;
import net.guts.gui.task.blocker.InputBlocker;
import net.guts.gui.task.blocker.InputBlockers;
import net.guts.gui.task.blocker.ModalDialogInputBlocker;
import net.guts.gui.util.ListenerDispatchProxy;
import net.guts.gui.util.ListenerEdtProxy;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.internal.Nullable;

/**
 * Defined as a "group of {@link Task}s, executed by the same {@link ExecutorService}, 
 * embedded in the same {@link InputBlocker}", a {@code TasksGroup} is the central
 * API of Guts-GUI tasks management system.
 * <p/>
 * Once you got a {@code TasksGroup} instance, you can:
 * <ul>
 * <li>add {@link Task}s to it (with or without an associated {@link TaskListener})</li>
 * <li>add a "global" {@code TaskListener<Object>} that will be notified of feedback and
 * status of all existing {@code Task}s in this {@code TasksGroup}.</li>
 * <li>start execution of all {@code Task}s in this {@code TasksGroup}</li>
 * <li>request for cancelling execution of all pending {@code Task}s in this 
 * {@code TasksGroup}</li>
 * </ul>
 * <p/>
 * A {@code TasksGroup} can only be created by the {@link TasksGroupFactory} service.
 * It is to be used only as "one-shot", i.e. you can't execute it more than once.
 * <p/>
 * It is important to notice that the list of {@code Task}s in a {@code TasksGroup} can
 * be quite dynamic: not only can you add new {@code Task}s <b>before</b> starting 
 * execution, but you can also add {@code Task}s <b>during</b> execution, e.g. you
 * can add a new {@code Task} to the {@code TasksGroup} from another {@code Task},
 * currently executing.
 * <p/>
 * Do note, however, that you can't add {@code Task}s to a {@code TasksGroup} which
 * execution is finished (or was cancelled).
 *
 * @see Task
 * @see TaskListener
 * @see TasksGroupFactory
 * @author Jean-Francois Poilpret
 */
public class TasksGroup
{
	/**
	 * Specify whether execution of a {@link TasksGroup} should be cancellable
	 * or not. Defined at {@code TasksGroup} construction time with 
	 * {@link TasksGroupFactory#newTasksGroup}. This can be used by 
	 * {@link InputBlocker}s that can give users an opportunity to cancel tasks 
	 * (e.g. {@link ModalDialogInputBlocker}).
	 */
	public enum Execution
	{
		/**
		 * The {@code TasksGroup} can be canceled during its execution.
		 */
		CANCELLABLE,
		
		/**
		 * The {@code TasksGroup} cannot be canceled once execution has started.
		 */
		NOT_CANCELLABLE
	}
	
	// We don't care about the number of parameters because this constructor is injected
	// Only 4 parameters are explicitly passed (through the Factory API)
	//CSOFF: ParameterNumberCheck
	@Inject TasksGroup(ExecutorServiceRegistry executorRegistry, Injector injector,
		ResourceInjector resourceInjector, DefaultExecutorHolder holder, 
		@Assisted String name, @Assisted Execution execution,
		@Assisted @Nullable InputBlocker blocker, 
		@Assisted @Nullable ExecutorService executor)
	{
		_name = name;
		_execution = execution;
		// Use default executor if needed
		ExecutorService actualExecutor = (executor != null ? executor : holder._executor);
		executorRegistry.registerExecutor(actualExecutor);
		// Use default blocker if needed
		InputBlocker actualBlocker = (blocker != null ? blocker : InputBlockers.noBlocker());
		// Make sure InputBlocker is injected (we can't make sure of that fact before)
		injector.injectMembers(actualBlocker);
		resourceInjector.injectInstance(blocker);
		_executor = new TasksGroupExecutor(
			this, actualBlocker, actualExecutor, _edtGroupListener);
	}
	//CSON: ParameterNumberCheck

	/**
	 * Indicates if this {@code TasksGroup} can be cancelled after {@code Task}s 
	 * execution has started. This depends on parameters passed at construction time, in
	 * {@link TasksGroupFactory#newTasksGroup(String, boolean, ExecutorService, InputBlocker)}.
	 * <p/>
	 * This method would generally be used by an {@link InputBlocker} implementation that
	 * offers a UI to allow the end user to cancel this {@code TasksGroup} execution.
	 * 
	 * @return {@code true} if this {@code TasksGroup} can be cancelled
	 */
	public boolean isCancellable()
	{
		return _execution == Execution.CANCELLABLE;
	}

	/**
	 * Add a new {@code Task<T>} to this {@code TasksGroup}, optionally with a matching
	 * {@code TaskListener<T>}.
	 * <p/>
	 * If this {@code TasksGroup} has not started execution yet, then {@code task} is 
	 * just added to the list of tasks waiting for execution. Otherwise, {@code task}
	 * is submitted for execution to the {@link ExecutorService} used by this 
	 * {@code TasksGroup} (hence may start immediately or be queued).
	 * <p/>
	 * {@code listener} will be notified of progress and status changes of {@code task}
	 * during its execution.
	 * 
	 * @param <T> the result type of {@link Task#execute} for {@code task}
	 * @param task the task to be added to this {@code TasksGroup} for grouped execution;
	 * if {@code null}, the method does nothing.
	 * @param listener a listener that will be notified of the progress and changes in 
	 * status of the added {@code task}; if {@code null}, {@code task} is added without
	 * any {@code TaskListener}.
	 * @return {@code this TasksGroup}, for chained calls (fluent API)
	 * @throws IllegalStateException if this {@code TasksGroup} has completed its 
	 * execution already
	 */
	public <T> TasksGroup add(Task<T> task, TaskListener<T> listener)
	{
		checkMutability("add Task");
		if (task == null)
		{
			return this;
		}
		TaskHandler<T> handler = new TaskHandler<T>(this, task, listener, _edtGroupListener);
		_handlers.add(handler);
		_tasks.add(handler);
		_executor.addTaskHandler(handler);
		return this;
	}
	
	/**
	 * Add a new {@code Task<T>} to this {@code TasksGroup}.
	 * <p/>
	 * If this {@code TasksGroup} has not started execution yet, then {@code task} is 
	 * just added to the list of tasks waiting for execution. Otherwise, {@code task}
	 * is submitted for execution to the {@link ExecutorService} used by this 
	 * {@code TasksGroup} (hence may start immediately or be queued).
	 * <p/>
	 * If {@code task} is a {@link AbstractTask} subtask, it will also be registered
	 * as a {@link TaskListener} of itself, and thus will be notified of its own progress 
	 * and status changes during its execution.
	 * 
	 * @param <T> the result type of {@link Task#execute} for {@code task}
	 * @param task the task to be added to this {@code TasksGroup} for grouped execution;
	 * if {@code null}, the method does nothing.
	 * @return {@code this TasksGroup}, for chained calls (fluent API)
	 * @throws IllegalStateException if this {@code TasksGroup} has completed its 
	 * execution already
	 */
	public <T> TasksGroup add(Task<T> task)
	{
		if (task == null)
		{
			return this;
		}
		if (AbstractTask.class.isAssignableFrom(task.getClass()))
		{
			return add(task, (AbstractTask<T>) task);
		}
		else
		{
			return add(task, null);
		}
	}
	
	/**
	 * Add a new {@code TaskListener<Object>} to this {@code TasksGroup}.
	 * <p/>
	 * {@code listener} will be notified of progress and status changes of all
	 * tasks managed by this {@code TasksGroup}.
	 * <p/>
	 * If called after tasks execution has started, then {@code listener} will
	 * be notified only for tasks that have not terminated yet.
	 * 
	 * @param listener a listener that will be notified of the progress and changes in 
	 * status of tasks; if {@code null}, {@code task} is added without
	 * any {@code TaskListener}.
	 * @return {@code this TasksGroup}, for chained calls (fluent API)
	 * @throws IllegalStateException if this {@code TasksGroup} has completed its 
	 * execution already
	 */
	public TasksGroup addGroupListener(TasksGroupListener listener)
	{
		if (listener != null)
		{
			checkMutability("add TasksGroupListener");
			_groupListeners.addListener(listener);
		}
		return this;
	}

	/**
	 * Starts execution of this {@code TasksGroup}, ie of all {@code Task}s added
	 * to this {@code TasksGroup}. Execution of all {@code Task}s is submitted to
	 * the {@link ExecutorService} that was passed to 
	 * {@link TasksGroupFactory#newTasksGroup(String, boolean, ExecutorService, InputBlocker)}.
	 * 
	 * @throws IllegalStateException if this {@code TasksGroup} has completed its 
	 * execution already
	 */
	public void execute()
	{
		_executor.execute();
	}
	
	/**
	 * Cancels execution of this {@code TasksGroup} ie of all current submitted 
	 * tasks. Cancellation has no impact on tasks that have already terminated.
	 * <p/>
	 * This method blocks until all tasks have stopped.
	 * <p/>
	 * This method does nothing if all tasks in this {@code TasksGroup} have 
	 * terminated already or if this {@code TasksGroup} has already been cancelled.
	 */
	public void cancel()
	{
		if (_execution == Execution.CANCELLABLE)
		{
			_executor.cancel();
		}
	}

	/**
	 * Indicates if this {@code TasksGroup} has been cancelled.
	 * 
	 * @return {@code true} if this {@code TasksGroup} has been or is currently 
	 * being cancelled.
	 */
	public boolean isCancelled()
	{
		return _executor.isCancelled();
	}

	/**
	 * Returns the name of this {@code TasksGroup} as passed to 
	 * {@code TasksGroupFactory.newTasksGroup(name, ...)}.
	 */
	public String name()
	{
		return _name;
	}
	
	public List<TaskInfo> tasks()
	{
		return _immutableTasks;
	}
	
	List<TaskHandler<?>> tasksHandlers()
	{
		return _handlers;
	}

	private void checkMutability(String operation)
	{
		if (_executor.isCancelled())
		{
			throw new IllegalStateException(
				"Can't " + operation + " to a cancelled TasksGroup!");
		}
		if (_executor.isFinished())
		{
			throw new IllegalStateException(
				"Can't " + operation + " to a terminated TasksGroup!");
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

	final private String _name;
	final private Execution _execution;
	final private List<TaskHandler<?>> _handlers = new CopyOnWriteArrayList<TaskHandler<?>>();
	final private List<TaskInfo> _tasks = new CopyOnWriteArrayList<TaskInfo>();
	final private List<TaskInfo> _immutableTasks = Collections.unmodifiableList(_tasks);

	final private ListenerDispatchProxy<TasksGroupListener> _groupListeners = 
		ListenerDispatchProxy.createProxy(TasksGroupListener.class);
	final private TasksGroupListener _edtGroupListener = ListenerEdtProxy.createProxy(
		TasksGroupListener.class, _groupListeners.notifier()).notifier();
	final private TasksGroupExecutor _executor;
}
