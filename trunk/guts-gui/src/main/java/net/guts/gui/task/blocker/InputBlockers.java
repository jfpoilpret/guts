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

/**
 * Utility class that helps create various kinds of {@link InputBlocker}s instances.
 *
 * @author Jean-Francois Poilpret
 */
public final class InputBlockers
{
	private InputBlockers()
	{
	}

	/**
	 * Always returns an "empty" {@code InputBlocker}, which does nothing at all.
	 */
	static public InputBlocker noBlocker()
	{
		return NO_BLOCKER;
	}

	/**
	 * Creates an {@link ActionInputBlocker} for {@code action}. With that 
	 * {@code InputBlocker}, during task execution, {@code action} is disabled.
	 */
	static public InputBlocker actionBlocker(GutsAction action)
	{
		return new ActionInputBlocker(action.action());
	}

	/**
	 * Creates a {@link ComponentInputBlocker} for the component that has triggered
	 * {@code action}. With that {@code InputBlocker}, during task execution, that
	 * component is disabled (but not {@code action} itself).
	 */
	static public InputBlocker componentBlocker(GutsAction action)
	{
		return new ComponentInputBlocker((Component) action.event().getSource());
	}

	/**
	 * Creates a {@link ComponentInputBlocker} that will block all {@code components}.
	 */
	static public InputBlocker componentsBlocker(Component... components)
	{
		return new ComponentInputBlocker(components);
	}

	/**
	 * Creates a {@link GlassPaneInputBlocker} that will block the Window that embeds
	 * the component which triggered {@code action}.
	 */
	static public InputBlocker windowBlocker(GutsAction action)
	{
		return windowBlocker(findComponentRoot((Component) action.event().getSource()));
	}

	/**
	 * Creates a {@link GlassPaneInputBlocker} that will block {@code root}.
	 */
	static public InputBlocker windowBlocker(JRootPane root)
	{
		return new GlassPaneInputBlocker(root);
	}

	/**
	 * Creates a {@link ModalDialogInputBlocker} that will show a modal dialog during
	 * task execution; the displayed modal dialog shows task progress and has a button
	 * to cancel tasks.
	 */
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
