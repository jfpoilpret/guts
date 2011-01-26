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

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.task.blocker.InputBlocker;

final class TasksGroupExecutor
{
	@SuppressWarnings("rawtypes") 
	TasksGroupExecutor(TasksGroup group, InputBlocker blocker, ExecutorService executor,
		TasksGroupListener groupListener)
	{
		_group = group;
		_blocker = blocker;
		_groupExecutor = new ExecutorCompletionService(executor);
		_groupListener = groupListener;
		_logger = LoggerFactory.getLogger(getClass().getSimpleName() + "-" + _group.name());
	}
	
	@SuppressWarnings("unchecked") 
	void addTaskHandler(TaskHandler<?> handler)
	{
		if (_state.get() == State.RUNNING)
		{
			handler.init(_groupExecutor.submit(handler));
		}
	}
	
	void execute()
	{
		if (!_state.compareAndSet(State.PENDING, State.STARTING))
		{
			_logger.error("execute() called while TasksGroup {} is already in {} state!",
				_group.name(), _state.get());
			throw new IllegalStateException("execute() called twice!");
		}
		
		// Make sure Task execution start is performed from the EDT
		if (SwingUtilities.isEventDispatchThread())
		{
			handleTasksExecution();
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					handleTasksExecution();
				}
			});
		}
	}
	
	void cancel()
	{
		if (_state.compareAndSet(State.RUNNING, State.CANCELLED))
		{
			for (TaskHandler<?> handler: _group.tasksHandlers())
			{
				handler.cancel();
			}
		}
	}
	
	boolean isCancelled()
	{
		return _state.get() == State.CANCELLED;
	}
	
	boolean isFinished()
	{
		return _state.get() == State.DONE;
	}
	
	@SuppressWarnings("unchecked") 
	private void handleTasksExecution()
	{
		// Block input right now (in EDT)
		_blocker.block(_group);
		for (TaskHandler<?> handler: _group.tasksHandlers())
		{
			handler.init(_groupExecutor.submit(handler));
		}
		_state.set(State.RUNNING);
		// Start a Thread to wait until all tasks are finished???
		Runnable handleFeedback = new Runnable()
		{
			@Override public void run()
			{
				handleTasksFeedback();
			}
		};
		_threadFactory.newThread(handleFeedback).start();
	}

	private void handleTasksFeedback()
	{
		while (!_group.tasksHandlers().isEmpty())
		{
			try
			{
				handleFuture(_groupExecutor.take());
			}
			catch (InterruptedException e)
			{
				_logger.debug("handleTasksFeedback", e);
			}
		}
		_state.compareAndSet(State.RUNNING, State.DONE);
		_groupListener.allTasksEnded(_group);
		// Make sure, at the end, to unblock input (must be in EDT)!
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override public void run()
			{
				_blocker.unblock(_group);
			}
		});
	}
	
	private void handleFuture(Future<?> future)
	{
		// Find the right task for that future and have it handle the result
		for (TaskHandler<?> handler: _group.tasksHandlers())
		{
			if (handler.handleResult(future))
			{
				// Remove handler from _group.tasks() to eventually end the waiting loop
				_group.tasksHandlers().remove(handler);
				return;
			}
		}
		_logger.error(
			"guts-gui internal error! handleFuture(): no TaskHandler found for this Future!");
	}
	
	static private enum State
	{
		PENDING,
		STARTING,
		RUNNING,
		CANCELLED,
		DONE
	}

	final private Logger _logger;
	final private TasksGroup _group;
	final private ThreadFactory _threadFactory = Executors.defaultThreadFactory();
	final private AtomicReference<State> _state = new AtomicReference<State>(State.PENDING);
	final private InputBlocker _blocker;
	final private TasksGroupListener _groupListener;
	@SuppressWarnings("rawtypes") final private CompletionService _groupExecutor;
}
