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
public abstract class TaskGroupAdapter extends TaskAdapter<Object> 
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
