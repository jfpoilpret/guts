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

package net.guts.gui.dialog2.template;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.RootPaneContainer;

import net.guts.gui.action.GutsAction;

final public class TemplateHelper
{
	private TemplateHelper()
	{
	}
	
	// returns true if view was compatible and has been directly modified
	// returns false if view was compatible and needs to be modified from scratch
	// throws exception is view was incompatible
	static public boolean checkCompatibleView(
		Container view, Class<? extends TemplateDecorator> type, GutsAction... actions)
	{
		if (isViewModified(view, type))
		{
			JButton[] buttons = new JButton[actions.length];
			// Search for similar buttons in modified view
			for (Component child: view.getComponents())
			{
				if (child instanceof JButton)
				{
					JButton button = (JButton) child;
					for (int i = 0; i < actions.length; i++)
					{
						if (isActionButton(button, actions[i]))
						{
							buttons[i] = button;
							break;
						}
					}
				}
			}
			// Check all buttons and actions are compatible, 
			// i.e. each action has a matching button and reciprocally
			for (int i = 0; i < actions.length; i++)
			{
				checkCompatibleActionButton(view, buttons[i], actions[i]);
			}
			
			// OK, all buttons are compatible, now replace actions where needed
			for (int i = 0; i < actions.length; i++)
			{
				replaceAction(buttons[i], actions[i]);
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	static private void replaceAction(JButton button, GutsAction action)
	{
		if (button != null)
		{
			button.setAction(action);
		}
	}
	
	static private void checkCompatibleActionButton(
		Container view, JButton button, GutsAction action)
	{
		if ((button == null) != (action == null))
		{
			String buttonName =  (button != null ? button.getName() : null);
			String actionName = (action != null ? action.name() : null);
			String msg = String.format(
				"Button '%s' isn't compatible with action '%s' in view '%s'",
				buttonName, actionName, view.getName());
			throw new IllegalArgumentException(msg);
		}
	}
	
	static private boolean isActionButton(JButton button, GutsAction target)
	{
		if (target != null)
		{
			return isActionButton(button, target.name());
		}
		else
		{
			return false;
		}
	}
	
	static private boolean isActionButton(JButton button, String name)
	{
		Action action = button.getAction();
		if (action instanceof GutsAction)
		{
			return name.equals(((GutsAction) action).name());
		}
		else
		{
			return false;
		}
	}
	
	static public boolean isViewModified(
		Container view, Class<? extends TemplateDecorator> type)
	{
		if (view instanceof JComponent)
		{
			JComponent jview = (JComponent) view;
			Class<?> marker = (Class<?>) jview.getClientProperty(MODIFIED_VIEW_MARKER);
			if (marker == null)
			{
				return false;
			}
			else if (marker == type)
			{
				return true;
			}
			else
			{
				String msg = String.format(
					"View '%s' was already modified by '%s', thus can't be modified by '%s'",
					view.getName(), marker.getName(), type.getName());
				throw new IllegalArgumentException(msg);
			}
		}
		else
		{
			return false;
		}
	}
	
	static public void setViewModified(Container view, Class<? extends TemplateDecorator> type)
	{
		if (view instanceof JComponent)
		{
			// Mark the view as already modified by the specified decorator type
			((JComponent) view).putClientProperty(MODIFIED_VIEW_MARKER, type);
		}
	}
	
	static public void close(RootPaneContainer container)
	{
		if (container instanceof Window)
		{
			((Window) container).dispose();
		}
		else if (container instanceof JInternalFrame)
		{
			((JInternalFrame) container).dispose();
		}
		else
		{
			// For applets, what can we possibly do, besides hide it?
			container.getRootPane().getParent().setVisible(false);
		}
	}
	
	static public JButton createButton(GutsAction action, Container view)
	{
		if (action != null)
		{
			JButton button = new JButton(action);
			button.setName(view.getName() + "-" + action.name());
			return button;
		}
		else
		{
			return null;
		}
	}
	
	static final private String MODIFIED_VIEW_MARKER = TemplateDecorator.class.getName();
}
