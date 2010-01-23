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

import javax.swing.JPopupMenu;
import javax.swing.JRootPane;

import net.guts.gui.action.GutsAction;
import net.guts.gui.task.TasksGroup;

public final class InputBlockers
{
	private InputBlockers()
	{
	}
	
	static public InputBlocker noBlocker()
	{
		return NO_BLOCKER;
	}
	
	static public InputBlocker actionBlocker(GutsAction action)
	{
		return new ActionInputBlocker(action.action());
	}
	
	static public InputBlocker componentBlocker(GutsAction action)
	{
		return new ComponentInputBlocker((Component) action.event().getSource());
	}
	
	static public InputBlocker componentsBlocker(Component... components)
	{
		return new ComponentInputBlocker(components);
	}
	
	static public InputBlocker windowBlocker(GutsAction action)
	{
		return windowBlocker(findComponentRoot((Component) action.event().getSource()));
	}
	
	static public InputBlocker windowBlocker(JRootPane root)
	{
		return new GlassPaneInputBlocker(root);
	}
	
	static public InputBlocker dialogBlocker()
	{
		return new ModalDialogInputBlocker();
	}
	
	//CSOFF: ParameterAssignmentCheck
	static private JRootPane findComponentRoot(Component component)
	{
		while (component != null)
		{
			if (component instanceof JRootPane)
			{
				return (JRootPane) component;
			}
			else if (component instanceof JPopupMenu)
			{
				return findComponentRoot(((JPopupMenu) component).getInvoker());
			}
			component = component.getParent();
		}
		return null;
	}
	//CSON: ParameterAssignmentCheck
	
	static final private InputBlocker NO_BLOCKER = new InputBlocker()
	{
		@Override public void block(TasksGroup tasks)
		{
		}

		@Override public void unblock(TasksGroup tasks)
		{
		}
	};
}
