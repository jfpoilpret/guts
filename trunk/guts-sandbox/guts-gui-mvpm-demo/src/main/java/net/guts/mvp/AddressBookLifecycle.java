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

package net.guts.mvp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.exception.HandlesException;
import net.guts.gui.exit.ExitController;
import net.guts.gui.window.JDialogConfig;
import net.guts.mvp.view.ContactsListDetailView;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AddressBookLifecycle implements AppLifecycleStarter
{
	static final private Logger _logger = LoggerFactory.getLogger(AddressBookLifecycle.class);
	
	@Inject
	public AddressBookLifecycle(ContactsListDetailView contactsView,
		DialogFactory dialogFactory, ExitController exitController)
	{
		_contactsView = contactsView;
		_dialogFactory = dialogFactory;
		_exitController = exitController;
	}

	@Override public void startup(String[] args)
	{
		// Show initiali view with contacts list
		_dialogFactory.showDialog(_contactsView, JDialogConfig.create().config());
		
		// Shutdown when dialog has closed
		_exitController.shutdown();
	}

	@Override public void ready()
	{
	}
	
	@HandlesException public boolean catches(Throwable e)
	{
		_logger.error("Uncaught exception has occurred!", e);
		return true;
	}
	
	final private ContactsListDetailView _contactsView;
	final private DialogFactory _dialogFactory;
	final private ExitController _exitController;
}
