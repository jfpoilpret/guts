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

import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

class EdtTaskDispatcher<T> implements TaskListener<T>
{
	EdtTaskDispatcher()
	{
	}
	
	void addListener(TaskListener<? super T> listener)
	{
		if (listener != null)
		{
			_listeners.add(listener);
		}
	}
	
	@Override public void failed(
		final TasksGroup group, final Task<? extends T> source, final Throwable cause)
	{
		invoke(new FireListener<T>()
		{
			@Override public void perform(TaskListener<? super T> listener)
			{
				listener.failed(group, source, cause);
			}
		});
	}

	@Override public void feedback(
		final TasksGroup group, final Task<? extends T> source, final String note)
	{
		invoke(new FireListener<T>()
		{
			@Override public void perform(TaskListener<? super T> listener)
			{
				listener.feedback(group, source, note);
			}
		});
	}

	@Override public void finished(final TasksGroup group, final Task<? extends T> source)
	{
		invoke(new FireListener<T>()
		{
			@Override public void perform(TaskListener<? super T> listener)
			{
				listener.finished(group, source);
			}
		});
	}

	@Override public void progress(
		final TasksGroup group, final Task<? extends T> source, final int rate)
	{
		invoke(new FireListener<T>()
		{
			@Override public void perform(TaskListener<? super T> listener)
			{
				listener.progress(group, source, rate);
			}
		});
	}

	@Override public void succeeded(
		final TasksGroup group, final Task<? extends T> source, final T result)
	{
		invoke(new FireListener<T>()
		{
			@Override public void perform(TaskListener<? super T> listener)
			{
				listener.succeeded(group, source, result);
			}
		});
	}

	@Override public void cancelled(final TasksGroup group, final Task<? extends T> source)
	{
		invoke(new FireListener<T>()
		{
			@Override public void perform(TaskListener<? super T> listener)
			{
				listener.cancelled(group, source);
			}
		});
	}

	@Override public void interrupted(final TasksGroup group, 
		final Task<? extends T> source, final InterruptedException cause)
	{
		invoke(new FireListener<T>()
		{
			@Override public void perform(TaskListener<? super T> listener)
			{
				listener.interrupted(group, source, cause);
			}
		});
	}

	private void invoke(final FireListener<T> fire)
	{
		invoke(new Runnable()
		{
			@Override public void run()
			{
				for (TaskListener<? super T> listener: _listeners)
				{
					fire.perform(listener);
				}
			}
		});
	}
	
	private void invoke(Runnable runnable)
	{
		if (!_listeners.isEmpty())
		{
			if (SwingUtilities.isEventDispatchThread())
			{
				runnable.run();
			}
			else
			{
				SwingUtilities.invokeLater(runnable);
			}
		}
	}
	
	static private interface FireListener<T>
	{
		void perform(TaskListener<? super T> listener);
	}
	
	private final List<TaskListener<? super T>> _listeners = 
		new LinkedList<TaskListener<? super T>>();
}
