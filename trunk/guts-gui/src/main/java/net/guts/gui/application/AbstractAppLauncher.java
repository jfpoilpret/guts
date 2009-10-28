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
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.GutsApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.injection.InjectionListeners;
import net.guts.gui.action.ActionModule;
import net.guts.gui.exception.ExceptionHandlingModule;
import net.guts.gui.exit.ExitModule;
import net.guts.gui.resource.ResourceModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

// Derive from this abstract launcher to start your Guts-GUI application
// or use one of the existing default implementations...
public abstract class AbstractAppLauncher
{
	static final private Logger _logger = LoggerFactory.getLogger(AbstractAppLauncher.class);
	static final private String ERROR_INIT_GUI = "initialization-error";

	//TODO javadoc!
	abstract protected void initModules(String[] args, List<Module> modules);

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
		// Now startup the GUI in the EDT
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				launchInEDT(args);
			}
		});
	}
	
	// CSOFF: IllegalCatchCheck
	private void launchInEDT(String[] args)
	{
		try
		{
			// Make sure we get all modules to initialize Guice injector
			final List<Module> modules = new ArrayList<Module>();
			modules.add(new ResourceModule());
			modules.add(new ActionModule());
			modules.add(new ExceptionHandlingModule());
			modules.add(new ExitModule());
			modules.add(new AppModule());
			List<Module> applicationModules = new ArrayList<Module>();
			initModules(args, applicationModules);
			modules.addAll(applicationModules);
			
			Injector injector = Guice.createInjector(modules);
			InjectionListeners.injectListeners(injector);
			
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
			_logger.error("Could not start application", e);
			fatalError(ERROR_INIT_GUI, e);
		}
	}
	// CSON: IllegalCatchCheck

	static private void fatalError(String id, Exception e)
	{
		//TODO create the necessary default bundle!
		// Fail graciously with Message Box!
		// Needs a special bundle with system-level fatal error messages
		ResourceBundle bundle = ResourceBundle.getBundle("guts-gui");
		String title = String.format(bundle.getString(id + ".title"), e);
		String message = String.format(bundle.getString(id + ".message"), e);
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}
	
	private class AppModule extends AbstractModule
	{
		@Override protected void configure()
		{
			// Make sure that we get ourselves injected: we'll soon need AppLifecycle!
			requestInjection(AbstractAppLauncher.this);
			// The following code will be removed when SAF is completely replaced
			GutsApplication application = new GutsApplication();
			requestInjection(application);
			bind(ApplicationContext.class).toInstance(
				new GutsApplicationContext(application));
		}
	}
	
	@Inject private AppLifecycleStarter _lifecycle;
}
