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

import java.awt.Component;

import net.guts.gui.task.TasksGroup;

public class ComponentInputBlocker implements InputBlocker
{
	public ComponentInputBlocker(Component... components)
	{
		_components = components;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action2.blocker.InputBlocker#block(net.guts.gui.action2.task.TasksSubmission)
	 */
	@Override public void block(TasksGroup tasks)
	{
		for (Component component: _components)
		{
			if (component != null)
			{
				component.setEnabled(false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action2.blocker.InputBlocker#unblock(net.guts.gui.action2.task.TasksSubmission)
	 */
	@Override public void unblock(TasksGroup tasks)
	{
		for (Component component: _components)
		{
			if (component != null)
			{
				component.setEnabled(true);
			}
		}
	}

	final private Component[] _components;
}
