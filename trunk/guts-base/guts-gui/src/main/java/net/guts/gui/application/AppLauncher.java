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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.common.injection.InjectionListeners;
import net.guts.common.type.TypeHelper;
import net.guts.gui.action.ActionModule;
import net.guts.gui.exception.ExceptionHandlingModule;
import net.guts.gui.exit.ExitModule;
import net.guts.gui.resource.ResourceModule;
import net.guts.gui.resource.Resources;
import net.guts.gui.session.SessionModule;
import net.guts.gui.session.Sessions;
import net.guts.gui.task.TasksModule;
import net.guts.gui.window.WindowModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

final class AppLauncher
{
	static final private Logger _logger = LoggerFactory.getLogger(AppLauncher.class);
	static final private String ERROR_INIT_GUI = "initialization-error";

	static void launch(String[] args, Class<?> mainClass, AbstractAppModuleInit init)
	{
		new AppLauncher(args, init, mainClass).launch();
	}

	private AppLauncher(String[] args, AbstractAppModuleInit init, Class<?> mainClass)
	{
		_args = (args != null ? args : new String[0]);
		_appInit = init;
		_mainClass = mainClass;
	}
	
	final protected void launch()
	{
		// Now startup the GUI in the EDT
		EventQueue.invokeLater(new Runnable()
		{
			@Override public void run()
			{
				launchInEDT();
			}
		});
	}
	
	// CSOFF: IllegalCatchCheck
	private void launchInEDT()
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
			_appInit.initModules(_args, applicationModules);
			modules.addAll(applicationModules);
			
			Injector injector = Guice.createInjector(modules);
			InjectionListeners.injectListeners(injector);
			
			// Now we can start the GUI initialization
			final AppLifecycleStarter lifecycle = 
				injector.getInstance(AppLifecycleStarter.class);
			lifecycle.startup(_args);

			// Call back to AppModuleInit (useful for applets)
			_appInit.afterStartup();
			
			// Then wait for the EDT to finish processing all events
			EdtHelper.waitForIdle(new Runnable()
			{
				@Override public void run()
				{
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

	static private void fatalError(String id, Exception e)
	{
		// Fail graciously with Message Box!
		// Needs a special bundle with system-level fatal error messages
		String pack = TypeHelper.getPackagePath(AppLauncher.class);
		ResourceBundle bundle = ResourceBundle.getBundle(pack + "/guts-gui");
		String title = String.format(bundle.getString(id + ".title"), e);
		String message = String.format(bundle.getString(id + ".message"), e);
		JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}
	
	private class AppModule extends AbstractModule
	{
		@Override protected void configure()
		{
			// Bind the application class for SessionManager
			Sessions.bindApplicationClass(binder(), _mainClass);
			// Bind the generic Application actions
			bind(GutsApplicationActions.class).asEagerSingleton();
			// Provide default resource values for common stuff: GutsApplicationActions
			Resources.bindPackageBundles(
				binder(), GutsApplicationActions.class, GutsGuiResource.PATH);
		}
	}

	final private String[] _args;
	final private AbstractAppModuleInit _appInit;
	final private Class<?> _mainClass;
}
