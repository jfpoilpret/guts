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

class TaskHandler<T> implements Callable<T>, FeedbackController
{
	TaskHandler(TasksGroup group, Task<T> task, TaskListener<? super T> listener)
	{
		_group = group;
		_task = task;
		_listener = new EdtTaskDispatcher<T>();
		addListener(listener);
	}
	
	void addListener(TaskListener<? super T> listener)
	{
		_listener.addListener(listener);
	}
	
	@Override public T call() throws Exception
	{
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
		_listener.progress(_group, _task, value);
	}
	
	@Override public void setFeedback(String note)
	{
		_listener.feedback(_group, _task, note);
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
			_listener.succeeded(_group, _task, _future.get());
		}
		catch (InterruptedException e)
		{
			_listener.interrupted(_group, _task, e);
		}
		catch (CancellationException e)
		{
			_listener.cancelled(_group, _task);
		}
		catch (ExecutionException e)
		{
			_listener.failed(_group, _task, e.getCause());
		}
		finally
		{
			_listener.finished(_group, _task);
		}
	}
	
	void init(Future<T> future)
	{
		_future = future;
	}
	
	static private final int MIN_PROGRESS = 0;
	static private final int MAX_PROGRESS = 100;
	
	final private TasksGroup _group;
	final private Task<T> _task;
	final private EdtTaskDispatcher<T> _listener;
	private Future<T> _future;
}
