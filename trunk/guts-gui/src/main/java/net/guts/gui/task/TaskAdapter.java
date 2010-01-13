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

//CSOFF: AbstractClassNameCheck
public abstract class TaskAdapter<T> implements TaskListener<T>
{
	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#progress(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, int)
	 */
	@Override public void progress(TasksGroup group, Task<? extends T> source, int rate)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#feedback(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, java.lang.String)
	 */
	@Override public void feedback(TasksGroup group, Task<? extends T> source, String note)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#succeeded(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, java.lang.Object)
	 */
	@Override public void succeeded(TasksGroup group, Task<? extends T> source, T result)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#failed(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, java.lang.Throwable)
	 */
	@Override public void failed(TasksGroup group, Task<? extends T> source, Throwable cause)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#finished(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task)
	 */
	@Override public void finished(TasksGroup group, Task<? extends T> source)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#cancelled(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task)
	 */
	@Override public void cancelled(TasksGroup group, Task<? extends T> source)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.task.TaskListener#interrupted(net.guts.gui.task.TasksGroup, net.guts.gui.task.Task, java.lang.InterruptedException)
	 */
	@Override public void interrupted(
		TasksGroup group, Task<? extends T> source, InterruptedException cause)
	{
	}
}
//CSON: AbstractClassNameCheck
