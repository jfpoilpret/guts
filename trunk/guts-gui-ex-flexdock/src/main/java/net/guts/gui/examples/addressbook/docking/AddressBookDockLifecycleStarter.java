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

package net.guts.gui.examples.addressbook.docking;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.action.ActionRegistrationManager;
import net.guts.gui.action.GutsAction;
import net.guts.gui.application.docking.DockingLifecycle;
import net.guts.gui.application.docking.OpenViewAction;

import net.guts.gui.examples.addressbook.AddressBookMenuBar;
import net.guts.gui.exception.HandlesException;
import net.guts.gui.menu.MenuFactory;
import net.guts.gui.message.MessageFactory;

import com.google.inject.Inject;

public class AddressBookDockLifecycleStarter extends DockingLifecycle
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(AddressBookDockLifecycleStarter.class);
	
	@Inject
	public AddressBookDockLifecycleStarter(
		AddressBookMenuBar menuBar, 
		MenuFactory menuFactory, ActionRegistrationManager actionManager,
		MessageFactory messageFactory)
	{
		_menuBar = menuBar;
		// Add Views menu
		_menuBar.add(menuFactory.createMenu("viewsMenu", 
			createViewAction(actionManager, Views.ContactList),
			createViewAction(actionManager, Views.ContactDetail),
			createViewAction(actionManager, Views.ContactPicture)));
		_messageFactory = messageFactory;
	}
	
	private GutsAction createViewAction(ActionRegistrationManager manager, Views view)
	{
		OpenViewAction action = new OpenViewAction(view.name());
		manager.registerAction(action);
		return action;
	}
	
	/* (non-Javadoc)
	 * @see net.guts.gui.application.docking.DockingLifecycle#initMainFrame(javax.swing.JFrame)
	 */
	@Override protected void initMainFrame(JFrame frame)
	{
		frame.setJMenuBar(_menuBar);
	}
	
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

	final private AddressBookMenuBar _menuBar;
	final private MessageFactory _messageFactory;
}
