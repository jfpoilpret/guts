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

import javax.swing.JApplet;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.injection.InjectionListeners;
import net.guts.common.type.TypeHelper;
import net.guts.gui.action.ActionModule;
import net.guts.gui.exception.ExceptionHandlingModule;
import net.guts.gui.exit.ExitController;
import net.guts.gui.exit.ExitModule;
import net.guts.gui.exit.ExitPerformer;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;
import net.guts.gui.session.SessionModule;
import net.guts.gui.session.Sessions;
import net.guts.gui.task.TasksModule;
import net.guts.gui.window.JAppletConfig;
import net.guts.gui.window.StatePolicy;
import net.guts.gui.window.WindowController;
import net.guts.gui.window.WindowModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Providers;

/**
 * This is the starting class to create a Guts-GUI based application.
 * It is used to bootstrap any Guts-GUI application.
 * {@code AbstractApplication} must be derived into your application's
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
 * public class AddressBookMain extends AbstractApplication
 * {
 *     public static void main(String[] args)
 *     {
 *         new AddressBookMain().launch(args);
 *     }
 *     
 *     &#64;Override protected void initModules(String[] args, List&lt;Module&gt; modules)
 *     {
 *         modules.add(new MessageModule());
 *         modules.add(new AddressBookModule());
 *     }
 *     
 *     private class AddressBookModule extends AbstractModule
 *     {
 *         &#64;Override protected void configure()
 *         {
 *             bind(AppLifecycleStarter.class)
 *                 .to(AddressBookLifecycleStarter.class).asEagerSingleton();
 *             // Add binding for "contact selection" events
 *             Events.bindChannel(binder(), Contact.class);
 *             bind(ContactActions.class).asEagerSingleton();
 *             // Setup ResourceModule root bundle
 *             Resources.bindRootBundle(binder(), getClass(), "resources");
 *         }
 *     }
 * }
 * </pre>
 * Note, in the example above, as a minimum requirement for the {@code initModules()}
 * method, you have to define a binding for {@link AppLifecycleStarter} to one of
 * your own classes; failing that, your application will show nothing and will
 * exit immediately!
 * <p/>
 * Guts-GUI also has default implementations that will dynamically find the
 * {@link com.google.inject.Module}s for your application, without any need
 * to subclass it. Refer to
 * <a href="net/guts/gui/application/support/package-summary.html">lifecycle support</a>.
 * <p/>
 * In case any error occurs during initialization process, it will be logged and an error
 * message will be displayed to the end user. For localization of these fatal error
 * message, {@code AbstractApplication} cannot use 
 * {@link net.guts.gui.resource.ResourceInjector} but must use a special 
 * {@link java.util.ResourceBundle}, which path (in classpath) is 
 * {@code `/net/guts/gui/application/guts-gui.properties`}. You can localize it by
 * providing files for specific {@link java.util.Locale}.
 *
 * @author Jean-Francois Poilpret
 */
public abstract class AbstractApplication extends JApplet
{
	private static final long serialVersionUID = 6486884374355341016L;
	static final private Logger _logger = LoggerFactory.getLogger(AbstractApplication.class);
	static final private String ERROR_INIT_GUI = "initialization-error";
	
	/**
	 * Override this method in your own launcher class. It will be called during the
	 * launch process, so that you can pass your {@link com.google.inject.Module}s
	 * to be used by {@code AbstractApplication} when creating the application
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

	/*
	 * (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	@Override final public void init()
	{
		_isApplet = true;
		launch(null);
	}

	/*
	 * (non-Javadoc)
	 * @see java.applet.Applet#destroy()
	 */
	@Override final public void destroy()
	{
		_exitController.shutdown();
	}

	/**
	 * Should be called from your {@code main} method to actually launch the 
	 * application.
	 * <p/>
	 * The method initializes Guice {@link com.google.inject.Injector} from default
	 * {@link com.google.inject.Module}s and {@code Module}s appended by
	 * {@link #initModules}.
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
	 * <li>{@link net.guts.gui.resource.ResourceModule}</li>
	 * <li>{@link net.guts.gui.session.SessionModule}</li>
	 * <li>{@link net.guts.gui.action.ActionModule}</li>
	 * <li>{@link net.guts.gui.exception.ExceptionHandlingModule}</li>
	 * <li>{@link net.guts.gui.exit.ExitModule}</li>
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
			@Override public void run()
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
			modules.add(new WindowModule());
			modules.add(new ResourceModule());
			modules.add(new SessionModule());
			modules.add(new ActionModule());
			modules.add(new TasksModule());
			modules.add(new ExceptionHandlingModule());
			modules.add(new ExitModule());
			modules.add(new AppModule());
			List<Module> applicationModules = new ArrayList<Module>();
			initModules(args, applicationModules);
			modules.addAll(applicationModules);
			
			Injector injector = Guice.createInjector(modules);
			InjectionListeners.injectListeners(injector);
			
			// Now we can start the GUI initialization
			final AppLifecycleStarter lifecycle = 
				injector.getInstance(AppLifecycleStarter.class);
			lifecycle.startup(args);

			// Call back to AppModuleInit (useful for applets)
			if (_isApplet)
			{
				_windowController.show(this, 
					JAppletConfig.create().state(StatePolicy.RESTORE_IF_EXISTS).config());
			}
			
			// Then wait for the EDT to finish processing all events
			EdtHelper.waitForIdle(new Runnable()
			{
				@Override public void run()
				{
					//TODO replace with events
					lifecycle.ready();
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
	
	private class AppModule extends AbstractModule
	{
		@Override protected void configure()
		{
			// Bind the application class for SessionManager
			Sessions.bindApplicationClass(binder(), AbstractApplication.this.getClass());
			// Bind the generic Application actions
			bind(GutsApplicationActions.class).asEagerSingleton();
			// Provide default resource values for common stuff: GutsApplicationActions
			Resources.bindPackageBundles(
				binder(), GutsApplicationActions.class, GutsGuiResource.PATH);
			// Ensure this is injected!
			requestInjection(this);
			if (_isApplet)
			{
				bind(JApplet.class).toInstance(AbstractApplication.this);
				// Don't allow System.exit() for an applet!
				bind(ExitPerformer.class).toInstance(new AppletExitPerformer());
			}
			else
			{
				bind(JApplet.class).toProvider(Providers.of((JApplet) null));
			}
		}
	}
	
	static private final class AppletExitPerformer implements ExitPerformer
	{
		@Override public void exitApplication()
		{
		}
	}
	
	static private void fatalError(String id, Exception e)
	{
		// Fail graciously with Message Box!
		// Needs a special bundle with system-level fatal error messages
		String pack = TypeHelper.getPackagePath(AbstractApplication.class);
		ResourceBundle bundle = ResourceBundle.getBundle(pack + "/guts-gui");
		String title = String.format(bundle.getString(id + ".title"), e);
		String message = String.format(bundle.getString(id + ".message"), e);
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}

	@Inject void init(
		WindowController windowController, ExitController exitController)
	{
		_windowController = windowController;
		_exitController = exitController;
	}

	private boolean _isApplet = false;
	private WindowController _windowController = null;
	private ExitController _exitController = null;
}
