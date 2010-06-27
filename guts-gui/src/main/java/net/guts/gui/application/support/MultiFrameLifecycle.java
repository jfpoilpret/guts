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
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.application.GutsApplicationActions;
import net.guts.gui.application.WindowController;
import net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.application.WindowController.StatePolicy;
import net.guts.gui.exit.ExitController;
import net.guts.gui.menu.MenuFactory;

import com.google.inject.Inject;

/**
 * Abstract {@link AppLifecycleStarter} that you can subclass if you are
 * developing a multiple frame based application. In such an application,
 * all frames have the "same weight", i.e. closing one in particular will
 * not shutdown the whole application; instead the application shuts down
 * only once all frames have been closed.
 * <p/>
 * The following snippet shows an example:
 * <pre>
 * public class MyAppLifecycleStarter extends MultiFrameLifecycle
 * {
 *     &#64;Override protected void startup(String[] args)
 *     {
 *         JFrame frame = new JFrame();
 *         frame.setName("MyFrame");
 *         frame.setJMenuBar(createMenuBar());
 *         frame.setContentPane(createContent());
 *         showFrame(frame);
 *     }
 * }
 * </pre>
 * You will have to make sure you bind you subclass to {@link AppLifecycleStarter}
 * from one of your {@link com.google.inject.Module}s:
 * <pre>
 *     &#64;Override protected void configure()
 *     {
 *         bind(AppLifecycleStarter.class)
 *             .to(MyAppLifecycleStarter.class).asEagerSingleton();
 *     }
 * </pre>
 *
 * @author Jean-Francois Poilpret
 */
//CSOFF: AbstractClassName
abstract public class MultiFrameLifecycle implements AppLifecycleStarter
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

	final protected void showFrame(JFrame frame)
	{
		_allFrames.add(frame);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(_closeListener);
		_windowController.show(
			frame, BoundsPolicy.PACK_AND_CENTER, StatePolicy.RESTORE_IF_EXISTS);
	}
	
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

	final private List<JFrame> _allFrames = new ArrayList<JFrame>();
	final private WindowListener _closeListener = new WindowAdapter()
	{
		@Override public void windowClosing(WindowEvent event)
		{
			// Exit only when last window gets closed
			_allFrames.remove(event.getWindow());
			if (_allFrames.isEmpty())
			{
				_exitController.shutdown();
			}
		}
	};
	private WindowController _windowController;
	private ExitController _exitController;
	private MenuFactory _menuFactory;
	private GutsApplicationActions _appActions;
}
//CSON: AbstractClassName
