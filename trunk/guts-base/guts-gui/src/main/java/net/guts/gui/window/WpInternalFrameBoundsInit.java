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

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;


import com.google.inject.Inject;

class WpInternalFrameBoundsInit 
extends AbstractWindowProcessor<JInternalFrame, JInternalFrame>
{
	@Inject WpInternalFrameBoundsInit(ActiveWindow activeWindow)
	{
		super(JInternalFrame.class, JInternalFrame.class);
		_activeWindow = activeWindow;
	}

	@Override protected void processRoot(
		JInternalFrame root, RootPaneConfig<JInternalFrame> config)
	{
		BoundsPolicy bounds = config.get(BoundsPolicy.class);
		// Initialize location and size according to policy
		if (bounds.mustPack())
		{
			root.pack();
		}
		if (bounds.mustCenter())
		{
			// First find where to add it
			Window active = _activeWindow.get();
			if (active instanceof JFrame)
			{
				JFrame frame = (JFrame) active;
				if (frame.getContentPane() instanceof JDesktopPane)
				{
					JDesktopPane desktop = (JDesktopPane) frame.getContentPane();
					// Calculate the coordinates in order to center the frame
					int w1 = desktop.getWidth();
					int w2 = root.getWidth();
					int x = Math.max((w2 - w1) / 2, 0);
					int h1 = desktop.getHeight();
					int h2 = root.getHeight();
					int y = Math.max((h2 - h1) / 2, 0);
					root.setLocation(x, y);
				}
			}
		}
	}
	
	final private ActiveWindow _activeWindow;
}
