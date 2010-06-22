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

package net.guts.gui.application.support;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.application.GutsApplicationActions;
import net.guts.gui.application.WindowController;
import net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.application.WindowController.StatePolicy;
import net.guts.gui.exit.ExitController;
import net.guts.gui.menu.MenuFactory;

import com.google.inject.Inject;

abstract public class SingleFrameLifecycle implements AppLifecycleStarter
{
	@Inject void init(
		WindowController windowController, ExitController exitController,
		MenuFactory menuFactory, GutsApplicationActions appActions)
	{
		_windowController = windowController;
		_exitController = exitController;
		_menuFactory = menuFactory;
		_appActions = appActions;
	}
	
	abstract protected void initFrame(JFrame mainFrame);
	
	final protected GutsApplicationActions appActions()
	{
		return _appActions;
	}
	
	final protected MenuFactory menuFactory()
	{
		return _menuFactory;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.AppLifecycleStarter#ready()
	 */
	@Override public void ready()
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.AppLifecycleStarter#startup(java.lang.String[])
	 */
	@Override final public void startup(String[] args)
	{
		JFrame mainFrame = new JFrame();
		mainFrame.setName("mainFrame");
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter()
		{
			@Override public void windowClosing(WindowEvent arg0)
			{
				_exitController.shutdown();
			}
		});

		// Initialize the main frame content
		initFrame(mainFrame);
		_windowController.show(
			mainFrame, BoundsPolicy.PACK_AND_CENTER, StatePolicy.RESTORE_IF_EXISTS);
	}

	private WindowController _windowController;
	private ExitController _exitController;
	private MenuFactory _menuFactory;
	private GutsApplicationActions _appActions;
}
