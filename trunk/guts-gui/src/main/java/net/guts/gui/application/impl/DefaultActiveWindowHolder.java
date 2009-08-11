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

package net.guts.gui.application.impl;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;

import net.guts.gui.application.ActiveWindowHolder;

/**
 * Default implementation of {@link ActiveWindowHolder} service.
 * <p/>
 * Normally, there is no need to override it or change it for another
 * implementation.
 * 
 * @author Jean-Francois Poilpret
 */
public class DefaultActiveWindowHolder implements ActiveWindowHolder
{
	public DefaultActiveWindowHolder()
	{
		_current = null;
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
		{
			public void eventDispatched(AWTEvent e)
			{
				DefaultActiveWindowHolder.this.eventDispatched(e);
			}
		}, AWTEvent.WINDOW_EVENT_MASK);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.application.ActiveWindowHolder#getActiveWindow()
	 */
	public Window getActiveWindow()
	{
		return _current;
	}

	private void eventDispatched(AWTEvent e)
	{
		switch (e.getID())
		{
			case WindowEvent.WINDOW_ACTIVATED:
				_current = ((WindowEvent) e).getWindow();
				break;

			case WindowEvent.WINDOW_DEACTIVATED:
				_current = null;
				break;
			
			default:
				// Do nothing (we are only interested in activation events)
				break;
		}
	}

	private Window _current;	
}
