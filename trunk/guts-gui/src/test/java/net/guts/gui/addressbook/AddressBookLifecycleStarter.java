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

package net.guts.gui.addressbook;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.jdesktop.application.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.addressbook.view.AddressBookMainView;
import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.application.WindowController;
import static net.guts.gui.application.WindowController.BoundsPolicy;
import net.guts.gui.exception.HandlesException;
import net.guts.gui.exit.ExitController;
import net.guts.gui.menu.MenuFactory;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;

public class AddressBookLifecycleStarter implements AppLifecycleStarter
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(AddressBookLifecycleStarter.class);
	
	//CSOFF: ParameterNumberCheck
	@Inject
	public AddressBookLifecycleStarter(WindowController windowController, 
		MenuFactory menuFactory, MessageFactory messageFactory,
		ResourceInjector injector, ExitController exitController, 
		AddressBookMainView mainView)
	{
		_mainView =  mainView;
		_windowController = windowController;
		_menuFactory = menuFactory;
		_messageFactory = messageFactory;
		_injector = injector;
		_exitController = exitController;
		
	}
	//CSON: ParameterNumberCheck
	
	@Override public void startup(String[] args)
	{
		// Create menus
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(_menuFactory.createMenu("fileMenu", "throwException", "quit"));
		menuBar.add(_menuFactory.createMenu("editMenu", "cut", "copy", "paste"));
		menuBar.add(_menuFactory.createMenu(
			"contactMenu", 
			"createContact", "createContactWithTabs", "createContactWithWizard", 
			"modifyContact", "modifyContactWithTabs", "modifyContactWithWizard", 
			"deleteContact"));
		menuBar.add(_menuFactory.createMenu("localeMenu", "french", "english"));
		JFrame mainFrame = new JFrame();
		mainFrame.setName("mainFrame");
		//TODO Guts-GUI should always provide this somehow
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter()
		{
			@Override public void windowClosing(WindowEvent arg0)
			{
				_exitController.shutdown();
			}
		});

		// Initialize the main frame content: 3 panels are there, separated by JSplitPanes
		mainFrame.setJMenuBar(menuBar);
		mainFrame.setContentPane(_mainView);
//		mainFrame.pack();
		_windowController.show(mainFrame, BoundsPolicy.PACK_AND_CENTER);
	}
	
	@Override public void ready()
	{
	}

	// Should be a better way to declare an object as an ActionSource (annotation + processing?)
//	@SuppressWarnings("unused")
//	@Inject private void initActionSources(
//		ActionManager manager, ContactActions contacts)
//	{
//		manager.addActionSource(contacts);
//	}

	// CSOFF: GenericIllegalRegexp
	// Handle exceptions on the EDT
	@HandlesException
	public boolean handle(Throwable e)
	{
		// Log the exception
		_logger.info("Exception has occurred!", e);
		// Show Message to the end user
		_messageFactory.showMessage("unexpected-error", e, e.getMessage());
		return true;
	}
	// CSON: GenericIllegalRegexp

	// Only to demonstrate the exception handling
	@Action public void throwException()
	{
		throw new IllegalArgumentException("Some message here");
	}
	
	@Action public void french()
	{
		_injector.setLocale(Locale.FRENCH);
	}

	@Action public void english()
	{
		_injector.setLocale(Locale.ENGLISH);
	}

	final private AddressBookMainView _mainView;
	final private WindowController _windowController;
	final private MenuFactory _menuFactory;
	final private MessageFactory _messageFactory;
	final private ResourceInjector _injector;
	final private ExitController _exitController;
}
