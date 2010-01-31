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

/**
 * An abstract adapter class for listening to progress and status changes of 
 * {@link Task}s executed by a {@link TasksGroup}. The methods in this class are
 * empty. This class exists as a convenience for creating listener objects.
 * <p/>
 * Extend this class to create a {@code TaskListener<T>} and override the methods
 * for the events of interest (if you implement the {@code TaskListener<T>}
 * interface, you have to define all of the methods in it; this abstract class
 * defines null methods for them all, so you can only have to define methods for 
 * events you care about).
 * <p/>
 * Create a listener object using the extended class and then register it with a 
 * {@code TasksGroup} using {@link TasksGroup#add(Task, TaskListener)} or
 * {@link TasksGroup#addGroupListener(TasksGroupListener)}.
 * 
 * @param <T> The type returned by the {@code Task<T>} that this 
 * {@link TaskListener} adapter wants to be notified about
 *
 * @author Jean-Francois Poilpret
 */
//CSOFF: AbstractClassNameCheck
public abstract class TaskAdapter<T> implements TaskListener<T>
{
	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#progress(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, int)
	 */
	@Override public void progress(TasksGroup group, TaskInfo source, int rate)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#feedback(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, java.lang.String)
	 */
	@Override public void feedback(TasksGroup group, TaskInfo source, String note)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#succeeded(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, java.lang.Object)
	 */
	@Override public void succeeded(TasksGroup group, TaskInfo source, T result)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#failed(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, java.lang.Throwable)
	 */
	@Override public void failed(TasksGroup group, TaskInfo source, Throwable cause)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#finished(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task)
	 */
	@Override public void finished(TasksGroup group, TaskInfo source)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#cancelled(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task)
	 */
	@Override public void cancelled(TasksGroup group, TaskInfo source)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#interrupted(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, java.lang.InterruptedException)
	 */
	@Override public void interrupted(
		TasksGroup group, TaskInfo source, InterruptedException cause)
	{
	}
}
//CSON: AbstractClassNameCheck
