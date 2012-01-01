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

import net.guts.common.cleaner.Cleaner;
import net.guts.common.ref.WeakRefSet;
import net.guts.common.ref.WeakRefSet.Performer;
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
		cleaner.addCleanable(_checkers);
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.exit.ExitController#registerExitCheckers(java.lang.Object)
	 */
	@Override public void registerExitChecker(ExitChecker checker)
	{
		_checkers.add(checker);
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
		boolean shutdown = _checkers.perform(new Performer<ExitChecker>()
		{
			@Override public boolean perform(ExitChecker checker)
			{
				return checker.acceptExit();
			}
		});
		if (shutdown)
		{
			// If we are here, we can shutdown the application
			// First send a shutdown event to all event listeners!
			_shutdownChannel.publish(null);
			// Then shutdown the application
			_exitPerformer.exitApplication();
		}
	}
	
	final private Channel<Void> _shutdownChannel;
	final private ExitPerformer _exitPerformer;
	final private WeakRefSet<ExitChecker> _checkers = WeakRefSet.create();
}
