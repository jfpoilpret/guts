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

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.RootPaneContainer;

import net.guts.common.type.Nullable;
import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.application.GutsApplicationActions;
import net.guts.gui.exit.ExitController;
import net.guts.gui.menu.MenuFactory;
import net.guts.gui.window.JFrameConfig;
import net.guts.gui.window.WindowController;

import com.google.inject.Inject;

/**
 * Abstract {@link AppLifecycleStarter} that you can subclass if you are
 * developing a single frame based application.
 * <p/>
 * The following snippet shows an example:
 * <pre>
 * public class MyAppLifecycleStarter extends SingleFrameLifecycle
 * {
 *     &#64;Override protected void initFrame(JFrame mainFrame)
 *     {
 *         mainFrame.setJMenuBar(createMenuBar());
 *         mainFrame.setContentPane(createMainView());
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
abstract public class SingleFrameLifecycle implements AppLifecycleStarter
{
	@Inject void init(
		WindowController windowController, ExitController exitController,
		MenuFactory menuFactory, GutsApplicationActions appActions,
		@Nullable JApplet applet)
	{
		_windowController = windowController;
		_exitController = exitController;
		_menuFactory = menuFactory;
		_appActions = appActions;
		_applet = applet;
	}

	/**
	 * Implement this method to setup the initial content of {@code mainFrame}.
	 * This includes setting up a {@linkplain JFrame#setContentPane content pane}
	 * and a {@linkplain JFrame#setJMenuBar menu bar}.
	 * <p/>
	 * {@code mainFrame} is named {@code "mainFrame"} and already initialized to 
	 * ensure that when the user closes it, the whole application is 
	 * {@linkplain ExitController#shutdown() shut down}. It will be 
	 * {@linkplain WindowController#show(JFrame, BoundsPolicy, StatePolicy) displayed}
	 * immediately after {@link #initFrame(JFrame)} returns.
	 * <p/>
	 * In {@code initFrame()}, you can use {@link #menuFactory()} and 
	 * {@link #appActions()} to build and set a {@link javax.swing.JMenuBar}, these
	 * are already injected in {@code this SingleFrameLifecycle} instance.
	 * 
	 * @param mainFrame the main frame to initialize
	 */
	abstract protected void initFrame(RootPaneContainer mainFrame);
	
	final protected GutsApplicationActions appActions()
	{
		return _appActions;
	}
	
	final protected MenuFactory menuFactory()
	{
		return _menuFactory;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.application.AppLifecycleStarter#startup(java.lang.String[])
	 */
	@Override final public void startup(String[] args)
	{
		RootPaneContainer rootContainer = _applet;
		if (_applet == null)
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
			rootContainer = mainFrame;
		}
		else
		{
			_applet.setName("mainApplet");
		}

		// Initialize the main frame content
		initFrame(rootContainer);
		if (rootContainer instanceof JFrame)
		{
			showMainFrame((JFrame) rootContainer, _windowController);
		}
	}
	
	protected void showMainFrame(JFrame mainFrame, WindowController windowController)
	{
		windowController.show(mainFrame, JFrameConfig.create().config());
	}
	
	private WindowController _windowController;
	private ExitController _exitController;
	private MenuFactory _menuFactory;
	private GutsApplicationActions _appActions;
	private JApplet _applet;
}
//CSON: AbstractClassName
