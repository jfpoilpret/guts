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

import java.util.List;

import javax.swing.JApplet;

import net.guts.gui.resource.ResourceInjector;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;

/**
 * This is the starting class to create a Guts-GUI based applet.
 * It is used to bootstrap any Guts-GUI applet.
 * {@code AbstractApplet} must be derived into your application's
 * applet class.
 * <p/>
 * Your applet class should then override {@link #initModules(List)} to provide 
 * the {@link com.google.inject.Module}s needed by your applet.
 * <p/>
 * As in the following example:
 * <pre>
 * public class AddressBookApplet extends AbstractApplet
 * {
 *     &#64;Override protected void initModules(List&lt;Module&gt; modules)
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
 * {@code AbstractApplet} automatically ensures that it is itself injectable 
 * anywhere needed; this allows you, for instance, to inject {@link javax.swing.JApplet}
 * in your {@link AppLifecycleStarter} implementing class and use it to set its
 * {@code contentPane}, as in the following example:
 * <pre>
 * public class AddressBookLifecycle extends AppLifecycleStarter
 * {
 *     &#64;Override public void startup(String[] args)
 *     {
 *         _applet.setContentPane(...);
 *         _applet.setJMenuBar(...);
 *     }
 *     
 *     &#64;Override public void ready() {}
 *     
 *     &#64;Inject private JApplet _applet;
 * }
 * </pre>
 * <p/>
 * In case any error occurs during initialization process, it will be logged and an error
 * message will be displayed to the end user. For localization of these fatal error
 * message, {@code AbstractApplet} cannot use 
 * {@link net.guts.gui.resource.ResourceInjector} but must use a special 
 * {@link java.util.ResourceBundle}, which path (in classpath) is 
 * {@code `/net/guts/gui/application/guts-gui.properties`}. You can localize it by
 * providing files for specific {@link java.util.Locale}.
 *
 * @author Jean-Francois Poilpret
 */
public abstract class AbstractApplet extends JApplet
{
	/*
	 * (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	@Override final public void init()
	{
		AppLauncher.launch(null, getClass(), new AppModuleInit()
		{
			@Override void initModules(String[] passedArgs, List<Module> modules)
			{
				modules.add(new AppletModule());
				AbstractApplet.this.initModules(modules);
			}
			
			@Override void afterStartup()
			{
				injectAppletResources();
			}
		});
	}

	/**
	 * Override this method in your own applet class. It will be called during the
	 * launch process, so that you can pass your {@link com.google.inject.Module}s
	 * to be used by {@code AbstractApplet} when creating the application
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
	 * @param modules a list of {@link com.google.inject.Module}s to which you
	 * must append your own {@code Module}s for Guice {@link com.google.inject.Injector}
	 * creation
	 */
	abstract protected void initModules(List<Module> modules);

	void injectAppletResources()
	{
		_injector.injectHierarchy(this);
	}
	
	@Inject void setResourceInjector(ResourceInjector injector)
	{
		_injector = injector;
	}
	
	// Special module to allow injection of this applet itself
	private class AppletModule extends AbstractModule
	{
		@Override protected void configure()
		{
			bind(JApplet.class).toInstance(AbstractApplet.this);
		}
	}
	
	private ResourceInjector _injector = null;
}
