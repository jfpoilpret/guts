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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.SwingWorker;

class TaskExecutor<T, V> extends SwingWorker<T, V> implements TaskController<V>
{
	TaskExecutor(Task<T, V> task, AbstractTaskService.TaskListeners<T, V> listeners,
		final InputBlocker blocker)
	{
		_task = task;
		_listeners = listeners;
		// Make sure to be notified when the thread is just starting
		addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName().equals("state"))
				{
					if (	evt.getOldValue() == StateValue.PENDING
						&&	evt.getNewValue() == StateValue.STARTED)
					{
						blocker.block();
						_listeners.fireDoInBackground();
					}
					else if (	evt.getOldValue() == StateValue.STARTED
							&&	evt.getNewValue() == StateValue.DONE)
					{
						blocker.unblock();
					}
				}
			}
		});
	}
	
	@Override public void publishIntermediateResults(V... chunks)
	{
		publish(chunks);
	}
	
	@Override protected T doInBackground() throws Exception
	{
		return _task.doInBackground(TaskExecutor.this);
	}
	
	@Override protected void process(List<V> chunks)
	{
		_listeners.fireProcess(chunks);
	}

	// CSOFF: IllegalCatchCheck
	@Override protected void done()
	{
		try
		{
			_listeners.fireSucceeded(get());
		}
		catch (Exception e)
		{
			_listeners.fireFailed(e);
		}
		finally
		{
			_listeners.fireFinished();
		}
	}
	// CSON: IllegalCatchCheck
	
	final private Task<T, V> _task;
	final private AbstractTaskService.TaskListeners<T, V> _listeners;
}
