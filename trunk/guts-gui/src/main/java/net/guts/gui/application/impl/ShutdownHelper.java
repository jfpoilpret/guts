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

import java.awt.EventQueue;
import java.awt.Frame;

/**
 * Utility class that avoids calling <code>System.exit()</code> from the AWT
 * Event Dispatch Thread (not good practice, and generates problems when using
 * abbot tools for GUI testing).
 * <p/>
 * IMPORTANT: note that using this good practice will prevent applications
 * launched from JWS to be shutdown correctly!
 *
 * @author Jean-Francois Poilpret
 */
final public class ShutdownHelper
{
	private ShutdownHelper()
	{
	}
	
	static public void	shutdown()
	{
		if (EventQueue.isDispatchThread())
		{
			disposeFrames();
		}
		else
		{
			// CSOFF: EmptyBlockCheck
			// CSOFF: IllegalCatchCheck
			try
			{
				EventQueue.invokeAndWait(new Runnable()
				{
					public void	run()
					{
						disposeFrames();
					}
				});
			}
			catch (Exception e)
			{
				// Don't do anything
			}
			// CSON: IllegalCatchCheck
			// CSON: EmptyBlockCheck
		}
	}

	static private void	disposeFrames()
	{
		Frame[] frames = Frame.getFrames();
		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i].isDisplayable())
			{
				frames[i].dispose();
			}
		}
	}
}
