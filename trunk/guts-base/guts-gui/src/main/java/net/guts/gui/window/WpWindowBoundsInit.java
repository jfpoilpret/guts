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

package net.guts.gui.window;

import java.awt.Window;

import javax.swing.RootPaneContainer;

import com.google.inject.Inject;

class WpWindowBoundsInit<V extends RootPaneContainer> 
extends AbstractWindowProcessor<Window, V>
{
	@Inject WpWindowBoundsInit(ActiveWindow activeWindow)
	{
		super(Window.class);
		_activeWindow = activeWindow;
	}

	@Override protected void processRoot(Window root, RootPaneConfig<V> config)
	{
		BoundsPolicy bounds = config._properties.get(BoundsPolicy.class);
		// Initialize location and size according to policy
		if (bounds.mustPack())
		{
			root.pack();
		}
		if (bounds.mustCenter())
		{
			root.setLocationRelativeTo(_activeWindow.get());
		}
	}
	
	final private ActiveWindow _activeWindow;
}
