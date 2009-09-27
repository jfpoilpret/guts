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

package net.guts.gui.application;

import java.awt.ActiveEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.PaintEvent;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class EdtHelper
{
	static final private Logger _logger = LoggerFactory.getLogger(EdtHelper.class);
	
	private EdtHelper()
	{
	}
	
	static void waitForIdle(final Runnable ready)
	{
		//TODO check this is called from EDT?
		
		final JPanel source = new JPanel();
		// Create a new Thread to post marker events to the EDT until no other 
		// event is queued
		Thread loop = new Thread()
		{
			@Override public void run()
			{
				//  Post marker events until the EDT is idle
				EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
				boolean emptyQueue = false;
				while (!emptyQueue)
				{
					IdleEvent event = new IdleEvent(source);
					queue.postEvent(event);
					synchronized (event)
					{
						while (!event._isDispatched)
						{
							try
							{
								event.wait();
							}
							catch (InterruptedException e)
							{
								// Not expected
								_logger.info("Unexpected exception", e);
							}
						}
						emptyQueue = event._isQueueEmpty;
					}
				}
				// Then call ready() from the EDT
				EventQueue.invokeLater(ready);
			}
		};
		loop.start();
	}
	
	static private class IdleEvent extends PaintEvent implements ActiveEvent
	{
		public IdleEvent(JPanel source)
		{
			super(source, UPDATE, null);
		}

		@Override public void dispatch()
		{
			EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
			synchronized(this)
			{
				_isQueueEmpty = (queue.peekEvent() == null);
				_isDispatched = true;
				notifyAll();
	        }
		}
		
        private boolean _isDispatched = false;
        private boolean _isQueueEmpty = false;
	}
}
