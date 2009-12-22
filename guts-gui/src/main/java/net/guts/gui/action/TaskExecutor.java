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

import java.util.List;

import javax.swing.SwingWorker;

class TaskExecutor<T, V> implements TaskResultPublisher<V>
{
	TaskExecutor(Task<T, V> task)
	{
		_task = task;
	}
	
	@Override public void publish(V... chunks)
	{
		_worker.push(chunks);
	}
	
	void execute()
	{
		_worker.execute();
	}
	
	private class Worker extends SwingWorker<T, V>
	{
		@Override protected T doInBackground() throws Exception
		{
			return _task.doInBackground(TaskExecutor.this);
		}
		
		@Override protected void process(List<V> chunks)
		{
			_task.process(chunks);
		}

		// CSOFF: IllegalCatchCheck
		@Override protected void done()
		{
			try
			{
				_task.succeeded(get());
			}
			catch (Exception e)
			{
				_task.failed(e);
			}
			finally
			{
				_task.finished();
			}
		}
		// CSON: IllegalCatchCheck
		
		void push(V... chunks)
		{
			publish(chunks);
		}
	}
	
	final private Worker _worker = new Worker();
	final private Task<T, V> _task;
}
