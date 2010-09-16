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

package net.guts.gui.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.cleaner.Cleaner;
import net.guts.common.ref.WeakRefSet;
import net.guts.common.ref.WeakRefSet.Performer;
import net.guts.event.Consumes;
import net.guts.gui.exit.ExitController;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
class ExecutorServiceRegistry
{
	static final private Logger _logger = 
		LoggerFactory.getLogger(ExecutorServiceRegistry.class);

	@Inject ExecutorServiceRegistry(Cleaner cleaner)
	{
		cleaner.addCleanable(_executors);
	}
	
	public void registerExecutor(ExecutorService executor)
	{
		_executors.add(executor);
	}
	
	@Consumes(topic = ExitController.SHUTDOWN_EVENT)
	public void shutdown(Void nothing)
	{
		_executors.perform(new Performer<ExecutorService>()
		{
			@Override public boolean perform(ExecutorService executor)
			{
				shutdown(executor);
				return true;
			}
		});
	}
	
	private void shutdown(ExecutorService executor)
	{
		// Disable new tasks from being submitted
		executor.shutdown();
		try
		{
			// Wait a while for existing tasks to terminate
			if (!executor.awaitTermination(WAIT_TIME_UNTIL_SHUTDOWN, TimeUnit.SECONDS))
			{
				_logger.info("Pending tasks could not be processed within {} seconds, " + 
					"now cancelling pending tasks...", WAIT_TIME_UNTIL_SHUTDOWN);
				// Cancel currently executing tasks
				executor.shutdownNow();
				// Wait a while for tasks to respond to being cancelled
				if (!executor.awaitTermination(WAIT_TIME_UNTIL_SHUTDOWN, TimeUnit.SECONDS))
				{
					_logger.info("On going tasks could not be cancelled within {} seconds, " +
						"quitting now anyway.", WAIT_TIME_UNTIL_SHUTDOWN);
				}
			}
		}
		catch (InterruptedException e)
		{
			_logger.info("Interrupted during waiting for ExecutorService shutdown", e);
			// (Re-)Cancel if current thread also interrupted
			executor.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

	static private final int WAIT_TIME_UNTIL_SHUTDOWN = 30;

	final private WeakRefSet<ExecutorService> _executors = WeakRefSet.create();
}
