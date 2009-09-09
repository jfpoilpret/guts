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

package net.guts.gui.exit;

import java.awt.EventQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.guts.common.cleaner.Cleanable;
import net.guts.common.cleaner.Cleaner;
import net.guts.event.Channel;
import net.guts.event.Event;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class ExitControllerImpl implements ExitController
{
	@Inject ExitControllerImpl(
		@Event(topic = SHUTDOWN_EVENT) Channel<Void> shutdownChannel, 
		ExitPerformer exitPerformer,
		Cleaner cleaner)
	{
		_shutdownChannel = shutdownChannel;
		_exitPerformer = exitPerformer;
		cleaner.addCleanable(new Cleanable()
		{
			@Override public void cleanup()
			{
				ExitControllerImpl.this.cleanup();
			}
		});
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.exit.ExitController#registerExitCheckers(java.lang.Object)
	 */
	@Override public void registerExitChecker(ExitChecker checker)
	{
		synchronized (_checkers)
		{
			_checkers.add(new WeakReference<ExitChecker>(checker));
		}
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.exit.ExitController#shutdown()
	 */
	@Override public void shutdown()
	{
		// Make sure we are in EDT!
		if (EventQueue.isDispatchThread())
		{
			performShutdown();
		}
		else
		{
			EventQueue.invokeLater(new Runnable()
			{
				@Override public void run()
				{
					performShutdown();
				}
			});
		}
	}
	
	private void performShutdown()
	{
		synchronized (_checkers)
		{
			for (WeakReference<ExitChecker> refChecker: _checkers)
			{
				ExitChecker checker = refChecker.get();
				if (checker != null && !checker.acceptExit())
				{
					return;
				}
			}
		}
		// If we are here, we can shutdown the application
		// First send a shutdown event to all event listeners!
		_shutdownChannel.publish(null);
		// Then shutdown the application
		_exitPerformer.exitApplication();
	}
	
	private void cleanup()
	{
		synchronized (_checkers)
		{
			Iterator<WeakReference<ExitChecker>> i = _checkers.iterator();
			while (i.hasNext())
			{
				if (i.next().get() == null)
				{
					i.remove();
				}
			}
		}
	}

	final private Channel<Void> _shutdownChannel;
	final private ExitPerformer _exitPerformer;
	final private List<WeakReference<ExitChecker>> _checkers = 
		new ArrayList<WeakReference<ExitChecker>>();
}
