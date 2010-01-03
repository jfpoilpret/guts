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

package net.guts.gui.action;

import java.awt.Component;
import java.awt.Window;

import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import net.guts.gui.util.SpinningAnimator;

public final class InputBlockers
{
	private InputBlockers()
	{
	}

	static public InputBlocker createNoBlocker()
	{
		return _noBlocker;
	}
	
	static public InputBlocker createActionBlocker(final GutsAction source)
	{
		return new AbstractInputBlocker()
		{
			@Override protected void setBlocking(boolean block)
			{
				source.action().setEnabled(!block);
			}
		};
	}

	static public InputBlocker createComponentBlocker(final GutsAction source)
	{
		final Component component = (Component) source.event().getSource();
		return new AbstractInputBlocker()
		{
			@Override protected void setBlocking(boolean block)
			{
				component.setEnabled(!block);
			}
		};
	}
	
	static public InputBlocker createWindowBlocker(GutsAction source)
	{
		return createWindowBlocker(source, null);
	}
	
	static public InputBlocker createWindowBlocker(
		GutsAction source, SpinningAnimator spinner)
	{
		// Find RootPane for component
		Component component = (Component) source.event().getSource();
		JRootPane root = findComponentRoot(component);
		if (root != null)
		{
			return new WindowInputBlocker(root, spinner);
		}
		else
		{
			return _noBlocker;
		}
	}
	
	static private JRootPane findComponentRoot(Component component)
	{
		if (component instanceof MenuElement)
		{
			return findMenuItemRoot((MenuElement) component);
		}
		else
		{
			return SwingUtilities.getRootPane(component);
		}
	}
	
	static private JRootPane findMenuItemRoot(MenuElement item)
	{
		// Find all Windows that are RootPaneContainers
		for (Window window: Window.getWindows())
		{
			if (window instanceof RootPaneContainer)
			{
				RootPaneContainer root = (RootPaneContainer) window;
				MenuElement menuBar = root.getRootPane().getJMenuBar();
				if (isItemContainedInMenu(item, menuBar))
				{
					return root.getRootPane();
				}
			}
		}
		return null;
	}
	
	static private boolean isItemContainedInMenu(MenuElement item, MenuElement menu)
	{
		if (menu != null)
		{
			for (MenuElement subitem: menu.getSubElements())
			{
				if (item == subitem || isItemContainedInMenu(item, subitem))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	static final private InputBlocker _noBlocker = new AbstractInputBlocker()
	{
		@Override protected void setBlocking(boolean block)
		{
		}
	};
}
