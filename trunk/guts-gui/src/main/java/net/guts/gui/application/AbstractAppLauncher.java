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
import net.guts.gui.session.SessionModule;
import net.guts.gui.session.Sessions;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * This is the starting class to create a Guts-GUI based application.
 * It is used to bootstrap any Guts-GUI application.
 * {@code AbstractAppLauncher} must be derived into your application's
 * main class.
 * <p/>
 * Your application main class should then:
 * <ul>
 * <li>define a {@code static void main(String[] args)} method and call
 * {@link #launch(String[])} in it </li>
 * <li>override {@link #initModules(String[], List)} to provide the 
 * {@link com.google.inject.Module}s needed by your application</li>
 * </ul>
 * As in the following example:
 * <pre>
 * TODO example with main(), initModules() and simplest module
 * </pre>
 * Note, in the example above, as a minimum requirement for the {@code initModules()}
 * method, you have to define a binding for {@link AppLifecycleStarter} to one of
 * your own classes; failing that, your application will show nothing and will
 * exit immediately!
 * <p/>
 * Guts-GUI also has default implementations that will dynamically find the
 * {@link com.google.inject.Module}s for your application, without any need
 * to subclass it.
 * Refer to (TODO later when these classes exist).
 * <p/>
 * TODO document fatal error (bootstrapping) handling and resource bundle for these errors.
 *
 * @author Jean-Francois Poilpret
 */
public abstract class AbstractAppLauncher
{
	static final private Logger _logger = LoggerFactory.getLogger(AbstractAppLauncher.class);
	static final private String ERROR_INIT_GUI = "initialization-error";

	/**
	 * Override this method in your own launcher class. It will be called during the
	 * launch process, so that you can pass your {@link com.google.inject.Module}s
	 * to be used by {@code AbstractAppLauncher} when creating the application
	 * {@link com.google.inject.Injector}.
	 * <p/>
	 * This method is called in Swing Event Dispatch Thread (EDT) but should not
	 * attempt to display anything by itself. Display code should go into
	 * {@link AppLifecycleStarter#startup(String[])} of your own implementation
	 * class for {@link AppLifecycleStarter}, which needs to be bound in one the
	 * {@link com.google.inject.Module}s you will add to {@code modules}.
	 * <p/>
	 * You can potentially add some initialization work (e.g. logging initialization)
	 * in this method.
	 * 
	 * @param args command line arguments that were passed to {@link #launch} from
	 * your main application launcher class {@code main} method
	 * @param modules a list of {@link com.google.inject.Module}s to which you
	 * must append your own {@code Module}s for Guice {@link com.google.inject.Injector}
	 * creation
	 */
	abstract protected void initModules(String[] args, List<Module> modules);

	/**
	 * Should be called from your {@code main} method to actually launch the 
	 * application.
	 * <p/>
	 * The method initializes Guice {@link com.google.inject.Injector} from default
	 * {@link com.google.inject.Module}s and {@code Module}s returned by
	 * {@link #getModules(String[])}.
	 * <p/>
	 * Then it gets the singleton instance implementing {@link AppLifecycleStarter}, and
	 * calls its methods in sequence (first {@link AppLifecycleStarter#startup} is called,
	 * then {@link AppLifecycleStarter#ready}. You are responsible for providing a Guice
	 * binding for {@link AppLifecycleStarter}.
	 * <p/>
	 * All this process is executed inside the EDT. If you have any initialization
	 * to perform outside the EDT, you have to do it before calling {@code launch}.
	 * <p/>
	 * Here is the list of default {@code Module}s used to create Guice {@code Injector}:
	 * <ul>
	 * <li>{@link ResourceModule}</li>
	 * <li>{@link SessionModule}</li>
	 * <li>{@link ActionModule}</li>
	 * <li>{@link ExceptionHandlingModule}</li>
	 * <li>{@link ExitModule}</li>
	 * <li>{@link net.guts.event.EventModule}</li>
	 * </ul>
	 * 
	 * @param args command line arguments that were passed through {@code main}
	 */
	final protected void launch(final String[] args)
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
			modules.add(new SessionModule());
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
			// Bind the application class for SessionManager
			Sessions.bindApplicationClass(binder(), AbstractAppLauncher.this.getClass());
			// The following code will be removed when SAF is completely replaced
			GutsApplication application = new GutsApplication();
			requestInjection(application);
			bind(ApplicationContext.class).toInstance(
				new GutsApplicationContext(application));
		}
	}
	
	@Inject private AppLifecycleStarter _lifecycle;
}
