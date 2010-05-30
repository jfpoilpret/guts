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

import com.google.inject.AbstractModule;
import com.google.inject.Module;

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
			@Override public void initModules(String[] passedArgs, List<Module> modules)
			{
				modules.add(new AppletModule());
				AbstractApplet.this.initModules(modules);
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

	// Special module to allow injection of this applet itself
	private class AppletModule extends AbstractModule
	{
		@Override protected void configure()
		{
			bind(JApplet.class).toInstance(AbstractApplet.this);
		}
	}
}
