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

package net.guts.gui.examples.addressbook;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.application.docking.DockingHelper;
import net.guts.gui.application.docking.DockingLifecycle;

import net.guts.gui.exception.HandlesException;
import net.guts.gui.message.MessageFactory;

import com.google.inject.Inject;

public class AddressBookLifecycleStarter extends DockingLifecycle
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(AddressBookLifecycleStarter.class);
	
	@Inject
	public AddressBookLifecycleStarter(
		AddressBookMenuBar menuBar, MessageFactory messageFactory)
	{
		_menuBar = menuBar;
		_messageFactory = messageFactory;
	}
	
	/* (non-Javadoc)
	 * @see net.guts.gui.application.docking.DockingLifecycle#initMainFrame(javax.swing.JFrame)
	 */
	@Override protected void initMainFrame(JFrame frame)
	{
		DockingHelper.trace(_logger, frame.getContentPane());
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
