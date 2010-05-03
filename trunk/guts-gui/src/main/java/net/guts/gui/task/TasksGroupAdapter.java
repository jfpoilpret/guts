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
 * all {@link Task}s executed by a {@link TasksGroup}. The methods in this class 
 * are empty. This class exists as a convenience for creating listener objects.
 * <p/>
 * Extend this class to create a {@code TasksGroupListener} and override the 
 * methods for the events of interest (if you implement the 
 * {@code TasksGroupListener} interface, you have to define all of the methods 
 * in it; this abstract class defines null methods for them all, so you can only 
 * have to define methods for events you care about).
 * <p/>
 * Create a listener object using the extended class and then register it with a 
 * {@code TasksGroup} using {@link TasksGroup#addGroupListener(TasksGroupListener)}.
 * 
 * @author Jean-Francois Poilpret
 */
//CSOFF: AbstractClassNameCheck
public abstract class TasksGroupAdapter extends TaskAdapter<Object> 
	implements TasksGroupListener
{
	/* (non-Javadoc)
	 * @see net.guts.gui.task.TasksGroupListener#allTasksEnded(net.guts.gui.task.TasksGroup)
	 */
	@Override public void allTasksEnded(TasksGroup group)
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.task.TasksGroupListener#taskAdded(net.guts.gui.task.TasksGroup, net.guts.gui.task.TaskInfo)
	 */
	@Override public void taskAdded(TasksGroup group, TaskInfo task)
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.task.TasksGroupListener#taskEnded(net.guts.gui.task.TasksGroup, net.guts.gui.task.TaskInfo)
	 */
	@Override public void taskEnded(TasksGroup group, TaskInfo task)
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.task.TasksGroupListener#taskStarted(net.guts.gui.task.TasksGroup, net.guts.gui.task.TaskInfo)
	 */
	@Override public void taskStarted(TasksGroup group, TaskInfo task)
	{
	}
}
//CSON: AbstractClassNameCheck
