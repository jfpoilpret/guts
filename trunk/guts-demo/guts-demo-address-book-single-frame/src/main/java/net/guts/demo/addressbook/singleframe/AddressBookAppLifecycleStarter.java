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

package net.guts.demo.addressbook.singleframe;

import javax.swing.RootPaneContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.demo.addressbook.singleframe.view.AddressBookMainView;
import net.guts.gui.application.support.SingleFrameLifecycle;

import net.guts.gui.exception.HandlesException;
import net.guts.gui.message.MessageFactory;

import com.google.inject.Inject;

public class AddressBookAppLifecycleStarter extends SingleFrameLifecycle
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(AddressBookAppLifecycleStarter.class);
	
	@Inject
	public AddressBookAppLifecycleStarter(
		AddressBookMainView mainView,
		AddressBookMenuBar menuBar,
		MessageFactory messageFactory)
	{
		_mainView =  mainView;
		_menuBar = menuBar;
		_messageFactory = messageFactory;
	}
	

	/* (non-Javadoc)
	 * @see net.guts.gui.application.support.SingleFrameLifecycle#initFrame(javax.swing.JFrame)
	 */
	@Override protected void initFrame(RootPaneContainer mainFrame)
	{
		mainFrame.getRootPane().setJMenuBar(_menuBar);
		mainFrame.setContentPane(_mainView);
	}
	
	// CSOFF: GenericIllegalRegexp
	// Handle exceptions on the EDT
	@HandlesException
	boolean handle(Throwable e)
	{
		// Log the exception
		_logger.info("Exception has occurred!", e);
		// Show Message to the end user
		_messageFactory.showMessage("unexpected-error", e, e.getMessage());
		return true;
	}
	// CSON: GenericIllegalRegexp

	final private AddressBookMainView _mainView;
	final private AddressBookMenuBar _menuBar;
	final private MessageFactory _messageFactory;
}
