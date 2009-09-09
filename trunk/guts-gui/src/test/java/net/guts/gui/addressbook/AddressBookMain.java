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

import javax.swing.JMenuBar;

import org.jdesktop.application.Action;

import net.guts.event.EventModule;
import net.guts.event.Events;
import net.guts.gui.action.ActionManager;
import net.guts.gui.addressbook.action.ContactActions;
import net.guts.gui.addressbook.domain.Contact;
import net.guts.gui.addressbook.view.AddressBookMainView;
import net.guts.gui.application.AbstractGuiceApplication;
import net.guts.gui.application.ExceptionHandler;
import net.guts.gui.menu.MenuFactory;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.util.GuiModuleHelper;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;

public class AddressBookMain extends AbstractGuiceApplication implements ExceptionHandler
{
	public static void main(String[] args)
	{
		launch(AddressBookMain.class, args);
	}

	@Override protected void preInit()
	{
		// Make sure frames & dialogs are decorated by the current PLAF (substance)
//		JFrame.setDefaultLookAndFeelDecorated(true);
//		JDialog.setDefaultLookAndFeelDecorated(true);
		// Workaround Sun bug #5079688 with JFrame/JDialog resize on Windows
		System.setProperty("sun.awt.noerasebackground", "true");
		// Enable automatic content selection on focus gained for all text fields
//		UIManager.put(LafWidget.TEXT_SELECT_ON_FOCUS, Boolean.TRUE);
		// We want a clean shutdown (NOTE: it will not work with JWS)
		setCleanShutdown();
		// Finally, add our specific module
		addModules(new EventModule(), new AddressBookModule());
	}
	
	@Override protected void startup()
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
		getMainFrame().setJMenuBar(menuBar);
		
		// Initialize the main frame content: 3 panels are there, separated by JSplitPanes
		getMainFrame().setContentPane(_view);
		getMainFrame().pack();
		show(getMainFrame());
	}

	// Should be a better way to declare an object as an ActionSource (annotation + processing?)
	@SuppressWarnings("unused")
	@Inject private void initActionSources(
		ActionManager manager, ContactActions contacts)
	{
		manager.addActionSource(contacts);
	}

	// CSOFF: GenericIllegalRegexp
	// Handle exceptions on the EDT
	public boolean handle(Throwable e)
	{
		// Log the exception
		e.printStackTrace();
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
	
	private class AddressBookModule extends AbstractModule
	{
		@Override protected void configure()
		{
			// Add binding to this as an ExceptionHandler
			GuiModuleHelper.bindExceptionHandler(binder()).toInstance(AddressBookMain.this);
			// Add binding for "contact selection" events
			Events.bindChannel(binder(), Contact.class);
		}
	}

	@Inject private AddressBookMainView _view;
	@Inject private MenuFactory _menuFactory;
	@Inject private MessageFactory _messageFactory;
}
