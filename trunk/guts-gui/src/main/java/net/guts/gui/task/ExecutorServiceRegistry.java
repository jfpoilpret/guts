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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.cleaner.Cleanable;
import net.guts.common.cleaner.Cleaner;
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
		cleaner.addCleanable(new Cleanable()
		{
			@Override public void cleanup()
			{
				ExecutorServiceRegistry.this.cleanup();
			}
		});
	}
	
	public void registerExecutor(ExecutorService executor)
	{
		synchronized (_executors)
		{
			_executors.add(new WeakReference<ExecutorService>(executor));
		}
	}
	
	@Consumes(topic = ExitController.SHUTDOWN_EVENT)
	public void shutdown(Void nothing)
	{
		synchronized (_executors)
		{
			for (WeakReference<ExecutorService> ref: _executors)
			{
				ExecutorService executor = ref.get();
				if (executor != null)
				{
					shutdown(executor);
				}
			}
		}
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

	private void cleanup()
	{
		synchronized (_executors)
		{
			Iterator<WeakReference<ExecutorService>> i = _executors.iterator();
			while (i.hasNext())
			{
				if (i.next().get() == null)
				{
					i.remove();
				}
			}
		}
	}
	
	static private final int WAIT_TIME_UNTIL_SHUTDOWN = 30;

	//TODO factor out the code for handling lists of weak references of any type!!!!
	final private List<WeakReference<ExecutorService>> _executors = 
		new ArrayList<WeakReference<ExecutorService>>();
}
