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

import net.guts.gui.exit.ExitController;
import net.guts.gui.exit.ExitPerformer;
import net.guts.gui.window.JAppletConfig;
import net.guts.gui.window.StatePolicy;
import net.guts.gui.window.WindowController;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Module;

class AppletSupport
{
	AppletSupport(AbstractApplication applet)
	{
		_applet = applet;
	}
	
	AbstractAppModuleInit appModuleInit()
	{
		return new AbstractAppModuleInit()
		{
			@Override void initModules(String[] passedArgs, List<Module> modules)
			{
				_applet.initModules(passedArgs, modules);
				modules.add(new AppletModule());
			}
			
			@Override void afterStartup()
			{
				_windowController.show(_applet, 
					JAppletConfig.create().state(StatePolicy.RESTORE_IF_EXISTS).config());
			}
		};
	}
	
	void destroy()
	{
		_exitController.shutdown();
	}

	@Inject void init(
		WindowController windowController, ExitController exitController)
	{
		_windowController = windowController;
		_exitController = exitController;
	}

	// Special module to allow injection of this applet itself
	private class AppletModule extends AbstractModule
	{
		@Override protected void configure()
		{
			// Ensure this is injected!
			requestInjection(AppletSupport.this);
			bind(JApplet.class).toInstance(_applet);
			// Don't allow System.exit() for an applet!
			bind(ExitPerformer.class).toInstance(new AppletExitPerformer());
		}
	}
	
	static private final class AppletExitPerformer implements ExitPerformer
	{
		@Override public void exitApplication()
		{
		}
	}

	final private AbstractApplication _applet;
	private WindowController _windowController = null;
	private ExitController _exitController = null;
}
