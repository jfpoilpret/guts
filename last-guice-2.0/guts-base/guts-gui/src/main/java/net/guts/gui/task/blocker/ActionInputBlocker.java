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

package net.guts.gui.task.blocker;

 import javax.swing.Action;

import net.guts.gui.task.TasksGroup;

/**
 * Simple {@link InputBlocker} which disables an {@link Action} for the whole duration
 * of tasks execution.
 *
 * @author Jean-Francois Poilpret
 */
public class ActionInputBlocker implements InputBlocker
{
	public ActionInputBlocker(Action action)
	{
		_action = action;
	}
	
	/* (non-Javadoc)
	 * @see net.guts.gui.action2.InputBlocker#block(net.guts.gui.action2.TasksSubmission)
	 */
	@Override public void block(TasksGroup tasks)
	{
		_action.setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action2.InputBlocker#unblock(net.guts.gui.action2.TasksSubmission, java.lang.Object)
	 */
	@Override public void unblock(TasksGroup tasks)
	{
		_action.setEnabled(true);
	}

	final private Action _action;
}
