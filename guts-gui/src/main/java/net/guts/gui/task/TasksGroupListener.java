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
 * Listener for progress and status changes of all {@link Task}s executed by a
 * {@link TasksGroup}.
 * <p/>
 * {@code TasksGroupListener}s are added to a {@code TasksGroup} which is then
 * responsible to notify them of task events.
 * <p/>
 * {@code TasksGroup} ensures that all notifications are performed in the EDT,
 * hence {@code TasksGroupListener} methods should be fast but can call methods on
 * any Swing component.
 * 
 * @author Jean-Francois Poilpret
 */
public interface TasksGroupListener extends TaskListener<Object>
{
	/**
	 * Called each time a new {@link Task} is added to a {@code TasksGroup} to
	 * which {@code this} listener was added.
	 * 
	 * @param group group to which {@code task} was added
	 * @param task the task that was added to {@code group}
	 */
	public void taskAdded(TasksGroup group, TaskInfo task);
	
	/**
	 * Called each time a {@link Task} is started inside a {@code TasksGroup} to
	 * which {@code this} listener was added.
	 * 
	 * @param group group in which {@code task} was started
	 * @param task the task that was just started in {@code group}
	 */
	public void taskStarted(TasksGroup group, TaskInfo task);
	
	/**
	 * Called each time a {@link Task} has ended inside a {@code TasksGroup} to
	 * which {@code this} listener was added. Task end may have occurred for any
	 * reason: task finished normally, was interrupted, cancelled or threw an
	 * exception.
	 * 
	 * @param group group in which {@code task} ended
	 * @param task the task that has just ended in {@code group}
	 */
	public void taskEnded(TasksGroup group, TaskInfo task);
	
	/**
	 * Called when all {@link Task}s inside a {@code TasksGroup} have ended. This
	 * is the last called method of {@code this} listener for a given {@code group}.
	 * 
	 * @param group group in which all tasks have ended
	 */
	public void allTasksEnded(TasksGroup group);
}
