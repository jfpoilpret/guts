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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("unchecked") 
abstract public class AbstractTaskService implements TaskService
{
	protected final <T, V> TaskListeners<T, V> getTaskListeners(Task<T, V> task)
	{
		return new TaskListeners<T, V>(task);
	}

	@Override public <T, V> void addTaskListener(
		Task<T, V> task, TaskListener<T, V> listener)
	{
		synchronized (_tasksListeners)
		{
			List<TaskListener> listeners =  _tasksListeners.get(task);
			if (listeners == null)
			{
				listeners = new ArrayList<TaskListener>(1);
				_tasksListeners.put(task, listeners);
			}
			if (!listeners.contains(listener))
			{
				listeners.add(listener);
			}
		}
	}

	@Override public <T, V> void removeTaskListener(
		Task<T, V> task, TaskListener<T, V> listener)
	{
		synchronized (_tasksListeners)
		{
			List<TaskListener> listeners =  _tasksListeners.get(task);
			if (listeners != null)
			{
				listeners.remove(listener);
				if (listeners.isEmpty())
				{
					_tasksListeners.remove(task);
				}
			}
		}
	}

	@Override public void addTaskListener(TaskListener<Object, Object> listener)
	{
		synchronized (_allListeners)
		{
			if (!_allListeners.contains(listener))
			{
				_allListeners.add(listener);
			}
		}
	}

	@Override public void removeTaskListener(TaskListener<Object, Object> listener)
	{
		synchronized (_allListeners)
		{
			_allListeners.remove(listener);
		}
	}

	protected final class TaskListeners<T, V>
	{
		private TaskListeners(Task<T, V> source)
		{
			_source = source;
			List<TaskListener> listeners = _tasksListeners.get(source);
			if (listeners != null)
			{
				_taskListeners = listeners;
			}
			else
			{
				_taskListeners = Collections.emptyList();
			}
		}
		
		public void fireDoInBackground()
		{
			for (TaskListener listener: _allListeners)
			{
				listener.doInBackground(_source);
			}
			if (_source instanceof TaskListener && !_taskListeners.contains(_source))
			{
				((TaskListener) _source).doInBackground(_source);
			}
			for (TaskListener listener: _taskListeners)
			{
				listener.doInBackground(_source);
			}
		}
		
		public void fireProcess(List<V> chunks)
		{
			for (TaskListener listener: _allListeners)
			{
				listener.process(_source, chunks);
			}
			if (_source instanceof TaskListener && !_taskListeners.contains(_source))
			{
				((TaskListener) _source).process(_source, chunks);
			}
			for (TaskListener listener: _taskListeners)
			{
				listener.process(_source, chunks);
			}
		}
		
		public void fireSucceeded(T result)
		{
			for (TaskListener listener: _allListeners)
			{
				listener.succeeded(_source, result);
			}
			if (_source instanceof TaskListener && !_taskListeners.contains(_source))
			{
				((TaskListener) _source).succeeded(_source, result);
			}
			for (TaskListener listener: _taskListeners)
			{
				listener.succeeded(_source, result);
			}
		}
		
		public void fireFailed(Throwable cause)
		{
			for (TaskListener listener: _allListeners)
			{
				listener.failed(_source, cause);
			}
			if (_source instanceof TaskListener && !_taskListeners.contains(_source))
			{
				((TaskListener) _source).failed(_source, cause);
			}
			for (TaskListener listener: _taskListeners)
			{
				listener.failed(_source, cause);
			}
		}
		
		public void fireFinished()
		{
			for (TaskListener listener: _allListeners)
			{
				listener.finished(_source);
			}
			if (_source instanceof TaskListener && !_taskListeners.contains(_source))
			{
				((TaskListener) _source).finished(_source);
			}
			for (TaskListener listener: _taskListeners)
			{
				listener.finished(_source);
			}
		}
		
		final private Task<?, ?> _source;
		final private List<TaskListener> _taskListeners;
	}

	private final List<TaskListener> _allListeners = new ArrayList<TaskListener>();
	private final Map<Task<?, ?>, List<TaskListener>> _tasksListeners = 
		new WeakHashMap<Task<?, ?>, List<TaskListener>>();
}
