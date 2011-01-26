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

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;

/**
 * This service tracks the current active {@link java.awt.Window}, ie the window
 * which is on front of any other of the application.
 * <p/>
 * You can inject this service wherever you need.
 *
 * @author Jean-Francois Poilpret
 */
public class ActiveWindow
{
	ActiveWindow()
	{
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
		{
			@Override public void eventDispatched(AWTEvent event)
			{
				if (event instanceof WindowEvent)
				{
					ActiveWindow.this.eventDispatched((WindowEvent) event);
				}
			}
		}, AWTEvent.WINDOW_EVENT_MASK);
	}
	
	/**
	 * Get the currently active window.
	 * 
	 * @return the front-most {@code Window} in the application, or {@code null} if there
	 * is no window currently displayed
	 */
	public Window get()
	{
		return _current;
	}

	private void eventDispatched(WindowEvent event)
	{
		switch (event.getID())
		{
			case WindowEvent.WINDOW_ACTIVATED:
			_current = event.getWindow();
			break;

			case WindowEvent.WINDOW_DEACTIVATED:
			_current = null;
			break;

			default:
			// Do nothing (we are only interested in the above events)
			break;
		}
	}

	private Window _current = null;
}
