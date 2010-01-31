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

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.guts.gui.util.ListenerDispatchProxy;
import net.guts.gui.util.ListenerEdtProxy;

import com.google.inject.TypeLiteral;

class TaskHandler<T> implements Callable<T>, FeedbackController, TaskInfo
{
	TaskHandler(TasksGroup group, Task<T> task, TaskListener<? super T> taskListener, 
		TasksGroupListener groupListener)
	{
		_group = group;
		_task = task;
		_taskListeners.addListener(taskListener);
		_groupListener = groupListener;
		_taskListeners.addListener(_groupListener);
		_groupListener.taskAdded(_group, this);
	}
	
	@Override public T call() throws Exception
	{
		_state = State.RUNNING;
		_groupListener.taskStarted(_group, this);
		return _task.execute(this);
	}
	
	@Override public boolean isCancelled()
	{
		return _future.isCancelled();
	}

	@Override public void setProgress(int current)
	{
		int value = Math.max(MIN_PROGRESS, current);
		value = Math.min(MAX_PROGRESS, current);
		_progress = value;
		_edtTaskListener.progress(_group, this, value);
	}
	
	@Override public void setFeedback(String note)
	{
		_feedback = note;
		_edtTaskListener.feedback(_group, this, note);
	}

	@Override public State state()
	{
		return _state;
	}
	
	@Override public int progress()
	{
		return _progress;
	}
	
	@Override public String feedback()
	{
		return _feedback;
	}

	boolean handleResult(Future<?> future)
	{
		if (_future == future)
		{
			handleResult();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	void cancel()
	{
		_future.cancel(true);
	}

	private void handleResult()
	{
		try
		{
			T result = _future.get();
			_progress = MAX_PROGRESS;
			_state = State.FINISHED;
			_edtTaskListener.succeeded(_group, this, result);
		}
		catch (InterruptedException e)
		{
			_state = State.CANCELLED;
			_edtTaskListener.interrupted(_group, this, e);
		}
		catch (CancellationException e)
		{
			_state = State.CANCELLED;
			_edtTaskListener.cancelled(_group, this);
		}
		catch (ExecutionException e)
		{
			_state = State.FAILED;
			_edtTaskListener.failed(_group, this, e.getCause());
		}
		finally
		{
			_edtTaskListener.finished(_group, this);
			_groupListener.taskEnded(_group, this);
		}
	}
	
	void init(Future<T> future)
	{
		_future = future;
	}
	
	static private final int MIN_PROGRESS = 0;
	static private final int MAX_PROGRESS = 100;
	
	final private TypeLiteral<TaskListener<? super T>> _type = 
		new TypeLiteral<TaskListener<? super T>>(){};

	final private TasksGroup _group;
	final private Task<T> _task;

	final private ListenerDispatchProxy<TaskListener<? super T>> _taskListeners = 
		ListenerDispatchProxy.createProxy(_type);
	final private TaskListener<? super T> _edtTaskListener = 
		ListenerEdtProxy.createProxy(_type, _taskListeners.notifier()).notifier();
	final private TasksGroupListener _groupListener;

	private Future<T> _future;
	private State _state = State.NOT_STARTED;
	private int _progress = MIN_PROGRESS;
	private String _feedback = "";
}
