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

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.application.ApplicationContext;

import net.guts.gui.exception.ExceptionHandlingModule;
import net.guts.gui.exit.ExitModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;

// Derive from this abstract launcher to start your Guts-GUI application
// or use one of the existing default implementations...
public abstract class AbstractAppLauncher
{
	abstract protected List<Module> getModules(String[] args);

	/**
	 * Should be called from your {@code main} method to actually launch the 
	 * application.
	 * <p/>
	 * The method initializes Guice {@link com.google.inject.Injector} from the 
	 * default {@link com.google.inject.Module} and {@code Module}s returned by
	 * {@link #getModules(String[])}.
	 * <p/>
	 * Then it gets the singleton instance implementing {@link AppLifecycleStarter}, and
	 * calls its methods in sequence. You are responsible for providing a Guice
	 * binding for {@link AppLifecycleStarter}.
	 * <p/>
	 * All this process is executed inside the EDT. If you have any initialization
	 * to perform outside the EDT, you have to do it before calling {@code launch}.
	 * 
	 * @param args command line arguments that were passed through {@code main}
	 */
	protected void launch(final String[] args)
	{
		//TODO Manage initialization errors somehow!
		
		// Now startup the GUI in the EDT
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				launchInEDT(args);
			}
		});
	}
	
	private void launchInEDT(String[] args)
	{
		try
		{
			// Make sure we get all modules to initialize Guice injector
			final List<Module> modules = new ArrayList<Module>();
			modules.add(new AppModule());
			modules.add(new ExceptionHandlingModule());
			modules.add(new ExitModule());
			modules.addAll(getModules(args));
			
			Guice.createInjector(modules);
			
			// Now we can start the GUI initialization
			_lifecycle.startup(args);
			
			// Then wait for the EDT to finish processing all events
			EdtHelper.waitForIdle(new Runnable()
			{
				@Override public void run()
				{
					_lifecycle.ready();
				}
			});
		}
		catch (Exception e)
		{
			//TODO fail graciously with Message Box!
		}
	}
	
	private class AppModule extends AbstractModule
	{
		@Override protected void configure()
		{
			// Make sure that we get ourselves injected: we'll soon need AppLifecycle!
			requestInjection(AbstractAppLauncher.this);
			bind(ApplicationContext.class).toInstance(new ApplicationContext(){});
		}
	}
	
	@Inject private AppLifecycleStarter _lifecycle;
}
