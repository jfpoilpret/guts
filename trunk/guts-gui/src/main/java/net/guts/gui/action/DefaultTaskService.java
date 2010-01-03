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

package net.guts.gui.action;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.event.Consumes;
import net.guts.gui.action.blocker.InputBlocker;
import net.guts.gui.action.blocker.InputBlockerFactory;
import net.guts.gui.exit.ExitController;

import com.google.inject.Inject;
import com.google.inject.Provider;

class DefaultTaskService extends AbstractTaskService
{
	static final private Logger _logger = LoggerFactory.getLogger(DefaultTaskService.class);
	
	@Inject DefaultTaskService(ExecutorServiceHolder executorHolder, 
		Map<Class<? extends Annotation>, Provider<InputBlockerFactory>> blockerFactories)
	{
		if (executorHolder._service != null)
		{
			_executor = executorHolder._service;
		}
		else
		{
			_executor = Executors.newSingleThreadExecutor();
		}
		_blockerFactories = blockerFactories;
	}
	
	@Override public <T, V> void execute(Task<T, V> task, InputBlocker blocker)
	{
		if (task != null)
		{
			TaskExecutor<T, V> executor = 
				new TaskExecutor<T, V>(task, getTaskListeners(task), blocker);
			_executor.execute(executor);
		}
	}
	
	public <T, V> void execute(
		Task<T, V> task, GutsAction source, Class<? extends Annotation> blocker)
	{
		Provider<InputBlockerFactory> provider = _blockerFactories.get(blocker);
		if (provider != null)
		{
			execute(task, provider.get().create(source));
		}
		else
		{
			_logger.error("execute() couldn't find any InputBlocker bound with annotation {}",
				blocker.getName());
		}
	}
	
	@Consumes(topic = ExitController.SHUTDOWN_EVENT) 
	public void shutdown(Void nothing)
	{
		// Disable new tasks from being submitted
		_executor.shutdown();
		try
		{
			// Wait a while for existing tasks to terminate
			if (!_executor.awaitTermination(WAIT_TIME_UNTIL_SHUTDOWN, TimeUnit.SECONDS))
			{
				_logger.info("Pending tasks could not be processed within {} seconds, " + 
					"now cancelling pending tasks...", WAIT_TIME_UNTIL_SHUTDOWN);
				// Cancel currently executing tasks
				_executor.shutdownNow();
				// Wait a while for tasks to respond to being cancelled
				if (!_executor.awaitTermination(WAIT_TIME_UNTIL_SHUTDOWN, TimeUnit.SECONDS))
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
			_executor.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
	
	// Workaround to Guice limitation on injection of optional args to constructors
	// See http://code.google.com/p/google-guice/wiki/FrequentlyAskedQuestions
	// "How can I inject optional parameters into a constructor?"
	//CSOFF: VisibilityModifierCheck
	static class ExecutorServiceHolder
	{
		@Inject(optional = true) @BindTaskServiceExecutor ExecutorService _service;
	}
	//CSON: VisibilityModifierCheck

	static private final int WAIT_TIME_UNTIL_SHUTDOWN = 30;
	
	final private ExecutorService _executor;
	final private Map<Class<? extends Annotation>, Provider<InputBlockerFactory>> 
		_blockerFactories;
}
