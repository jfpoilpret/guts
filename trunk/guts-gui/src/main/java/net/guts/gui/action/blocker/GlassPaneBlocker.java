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

package net.guts.gui.action.blocker;

import java.awt.Component;
import java.awt.Window;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.swing.JRootPane;
import javax.swing.MenuElement;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import net.guts.gui.action.GutsAction;

import com.google.inject.BindingAnnotation;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@BindingAnnotation
public @interface GlassPaneBlocker
{
}

class GlassPaneInputBlocker implements InputBlocker
{
	@Inject GlassPaneInputBlocker(@Assisted GutsAction source, DisabledGlassPane glassPane)
	{
		_root = findComponentRoot((Component) source.event().getSource());
		_blockingGlassPane = glassPane;
	}

	@Override public void block()
	{
		_savedGlassPane = _root.getGlassPane();
		_root.setGlassPane(_blockingGlassPane);
		_blockingGlassPane.activate();
	}

	@Override public void unblock()
	{
		_blockingGlassPane.deactivate();
		_root.setGlassPane(_savedGlassPane);
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

	final private DisabledGlassPane _blockingGlassPane;
	final private JRootPane _root;
	private Component _savedGlassPane;
}
