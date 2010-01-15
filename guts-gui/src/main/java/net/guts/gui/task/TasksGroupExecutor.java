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

//TODO does it really need to be public? Could be just a delegate for TasksGroup!
final public class TasksGroupExecutor
{
	@SuppressWarnings("unchecked")
	TasksGroupExecutor(TasksGroup group, InputBlocker blocker, ExecutorService executor)
	{
		_group = group;
		_blocker = blocker;
		_groupExecutor = new ExecutorCompletionService(executor);
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
	
	@SuppressWarnings("unchecked") 
	public void execute()
	{
		if (!_state.compareAndSet(State.PENDING, State.STARTING))
		{
			_logger.error("execute() called while TasksGroup {} is already in {} state!",
				_group.name(), _state.get());
			throw new IllegalStateException("execute() called twice!");
		}

		Runnable exec = new Runnable()
		{
			@Override public void run()
			{
				// Block input right now (in EDT)
				_blocker.block(_group);
				for (TaskHandler<?> handler: _group.tasks())
				{
					handler.init(_groupExecutor.submit(handler));
				}
				_state.set(State.RUNNING);
				// Start a Thread to wait until all tasks are finished???
				Runnable handleFeedback = new Runnable()
				{
					public void run()
					{
						handleTasksFeedback();
					}
				};
				_threadFactory.newThread(handleFeedback).start();
			}
		};

		// Make sure this is called from the EDT
		if (SwingUtilities.isEventDispatchThread())
		{
			exec.run();
		}
		else
		{
			SwingUtilities.invokeLater(exec);
		}
	}
	
	public void cancel()
	{
		if (_group.isCancellable() && _state.compareAndSet(State.RUNNING, State.CANCELLED))
		{
			for (TaskHandler<?> handler: _group.tasks())
			{
				handler.cancel();
			}
		}
	}
	
	public boolean isCancelled()
	{
		return _state.get() == State.CANCELLED;
	}
	
	private void handleTasksFeedback()
	{
		while (!_group.tasks().isEmpty())
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
		// Make sure, at the end, to unblock input (must be in EDT)!
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_blocker.unblock(_group);
			}
		});
	}
	
	private void handleFuture(Future<?> future)
	{
		// Find the right task for that future and have it handle the result
		for (TaskHandler<?> handler: _group.tasks())
		{
			if (handler.handleResult(future))
			{
				// Remove handler from _group.tasks() to eventually end the waiting loop
				_group.tasks().remove(handler);
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
	@SuppressWarnings("unchecked") final private CompletionService _groupExecutor;
}
