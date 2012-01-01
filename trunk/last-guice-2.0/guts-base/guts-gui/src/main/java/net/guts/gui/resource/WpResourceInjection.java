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

package net.guts.gui.resource;

import java.awt.Window;
import java.util.Locale;

import javax.swing.RootPaneContainer;

import net.guts.event.Consumes;
import net.guts.gui.window.RootPaneConfig;
import net.guts.gui.window.WindowProcessor;

import com.google.inject.Inject;

class WpResourceInjection implements WindowProcessor
{
	@Inject WpResourceInjection(ResourceInjector injector)
	{
		_injector = injector;
	}
	
	@Override 
	public <T extends RootPaneContainer> void process(T root, RootPaneConfig<T> config)
	{
		_injector.injectHierarchy(root.getRootPane().getParent());
	}

	@Consumes(priority = Integer.MIN_VALUE + 1)
	public void localeChanged(Locale locale)
	{
		for (Window window: Window.getWindows())
		{
			if (window.isVisible())
			{
				_injector.injectHierarchy(window);
				window.repaint();
				if (window instanceof RootPaneContainer)
				{
					((RootPaneContainer) window).getRootPane().revalidate();
				}
			}
		}
	}

	final private ResourceInjector _injector;
}
