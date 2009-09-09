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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceConverter;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Application.ExitListener;

import net.guts.gui.application.impl.CursorConverter;
import net.guts.gui.application.impl.CursorInfoConverter;
import net.guts.gui.application.impl.DefaultActiveWindowHolder;
import net.guts.gui.application.impl.ExceptionHandlerDispatcher;
import net.guts.gui.application.impl.ImageConverter;
import net.guts.gui.application.impl.ShutdownHelper;
import net.guts.gui.application.impl.SwingExceptionHandler;
import net.guts.gui.util.ResourceComponent;
import net.guts.gui.util.TableHelper;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

/**
 * Superclass for all Guice-enabled applications based on Swing Application
 * Framework (JSR-296).
 * <p/>
 * Your subclass just has to override {@link #preInit()} if it needs to
 * add Guice modules (by calling {@link #addModules(Module[])} or it needs any 
 * special initialization code before Guice Injector creation; if necessary, it 
 * may also override {@link #postInit()} which is called <b>after</b> the
 * Guice Injector has been initialized. Your subclass also needs to define the 
 * {@code main()} method as follows:
 * <pre>
 * static public void main(String[] args) {
 *     launch(YourApp.class, args);
 * }
 * </pre>
 * It is not recommended to add a constructor to your application class, but 
 * rather use standard lifecycle methods from JSR-296 or one of the additional
 * methods of this class: {@link #preInit()} or {@link #postInit()}.
 * <p/>
 * You may use Guice annotations to inject anything to your application, the
 * properties to be injected will be setup in all lifecycle methods starting
 * from {@link #postInit()} (but {@link #preInit()} or any constructor cannot 
 * count on those properties to be initialized).
 * 
 * @author Jean-Francois Poilpret
 */
abstract public class AbstractGuiceApplication extends SingleFrameApplication
{
	/**
	 * Override this method if you need to add your own Guice modules before
	 * creation of the Guice {@link Injector}.
	 * Modules are added by calling {@link #addModules(Module[])}.
	 * <p/>
	 * This method can also setup some logging facilities if needed. It can
	 * access command line arguments through {@link #getArguments()}.
	 */
	protected void preInit()
	{
		// Empty by default (meant to be overridden if needed)
	}
	
	/**
	 * Override this method if you need to perform further initialization after
	 * creation of the Guice {@link Injector}. When this method is invoked,
	 * this instance has already been injected by Guice so any injected fields
	 * are usable.
	 * <p/>
	 * In general, you won't need to override this method.
	 */
	protected void postInit()
	{
		// Empty by default (meant to be overridden if needed)
	}

	/**
	 * Adds Guice {@link Module}s that will be used to create the Guice
	 * {@link Injector}.
	 * <p/>
	 * You should call this method exclusively from the overridden 
	 * {@link #preInit()} method!
	 * 
	 * @param modules any number of instances implementing the {@link Module} 
	 * interface.
	 */
	final protected void addModules(Module... modules)
	{
		// Can be null when it's too late to add modules (Guice initialized already)
		if (_modules != null)
		{
			for (Module module: modules)
			{
				_modules.add(module);
			}
		}
		else
		{
			_logger.warning("addModules() called after PreInit(): " + 
				"added modules cannot be accounted for!");
		}
	}

	/**
	 * Get the command-line arguments for further processing if needed. Can be
	 * called from {@link #preInit()} or {@link #postInit()}.
	 * 
	 * @return the arguments that were passed in the command line
	 */
	final protected String[] getArguments()
	{
		return _args;
	}

	/**
	 * Sets whether a clean shutdown should be performed when quitting the
	 * application.
	 * <p/>
	 * A clean shutdown consists in disposing of all open frames (including
	 * invisible frames that are sometimes created by Swing as parents of
	 * parentless JDialog).
	 * <p/>
	 * A clean shutdown is advised except when you want to use Java Web Start,
	 * in which case the only way to shutdown the application is to call
	 * {@link System#exit(int)}. This is the reason why, by default, clean
	 * shutdown is disabled, but can be explicitly enabled by calling this
	 * method.
	 */
	final protected void setCleanShutdown()
	{
		_cleanShutdown = true;
	}

	/**
	 * This method is automatically called by Swing Application Framework and 
	 * should never be called directly.
	 */
	@Override final protected void initialize(String[] args)
	{
		// Add the default module for GUI
		_args = args;
		addModules(new GuiModule());
		// Register a few useful resource converters
		ResourceConverter.register(new ImageConverter());
		ResourceConverter.register(new CursorInfoConverter());
		ResourceConverter.register(new CursorConverter());

		// Give opportunity to subclass to start initialization (eg logging) and
		// add its own Guice modules
		preInit();

		// Initialize Guice
		_injector = Guice.createInjector(_modules);
		_modules = null;
		// Inject to this 
		//TODO check if this line is necessary (isn't binding to this enough?)
//		_injector.injectMembers(this);
		// Give opportunity to subclass to finish initialization
		postInit();
	}

	/**
	 * This method is automatically called by Swing Application Framework and 
	 * should never be called directly.
	 */
	@Override final protected void end()
	{
		if (_cleanShutdown)
		{
			ShutdownHelper.shutdown();
		}
		else
		{
			super.end();
		}
	}

	// Internal classes to manage Guice injection for some 
	private class GuiModule extends AbstractModule
	{
		@Override protected void configure()
		{
			// Make Application and Context injectable
			bind(ApplicationContext.class).toInstance(getContext());
			bind(AbstractGuiceApplication.class).toInstance(AbstractGuiceApplication.this);
			
			// Initialize Exception Handling
			requestStaticInjection(SwingExceptionHandler.class);
			Multibinder.newSetBinder(binder(), ExceptionHandler.class);
			bind(ExceptionHandler.class).to(ExceptionHandlerDispatcher.class);

			// Initialize ExitListener bindings
			Multibinder.newSetBinder(binder(), ExitListener.class);
			//TODO finish the work!
			
			// Initialize ActiveWindowHolder
			bind(ActiveWindowHolder.class)
				.to(DefaultActiveWindowHolder.class).asEagerSingleton();

			// Static injection of a few special classes
			requestStaticInjection(TableHelper.class, ResourceComponent.class);
		}
	}
	
	@Inject void initExitListeners()
	{
		
	}
	
	final private Logger _logger =
		Logger.getLogger(AbstractGuiceApplication.class.getName());

	private String[] _args;
	private List<Module> _modules = new ArrayList<Module>();
	private Injector _injector;
	private boolean _cleanShutdown = false;
}
