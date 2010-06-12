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

import java.util.Locale;

import javax.swing.JMenuBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.action.GutsAction;
import net.guts.gui.application.AppLifecycleStarter;
import net.guts.gui.application.GutsApplicationActions;

import net.guts.gui.examples.addressbook.action.ContactActions;
import net.guts.gui.examples.addressbook.action.TaskTestActions;
import net.guts.gui.examples.addressbook.view.AddressBookMainView;
import net.guts.gui.exception.HandlesException;
import net.guts.gui.menu.MenuFactory;
import net.guts.gui.message.MessageFactory;
import net.guts.gui.resource.ResourceInjector;

import com.google.inject.Inject;

//TODO Refactor to further separate concerns:
// - lifecycle			=> keep it here
// - menu construction	=> AddressBookMenu.class??
// - actions			=> AddressBookActions.class
// - error handling		=> ??
// => thus the number of dependencies (constructor-injected) will be reduced (good practice)
public class AddressBookLifecycleStarter implements AppLifecycleStarter
{
	static private final Logger _logger = 
		LoggerFactory.getLogger(AddressBookLifecycleStarter.class);
	
	//CSOFF: ParameterNumberCheck
	@Inject
	public AddressBookLifecycleStarter(
		AddressBookUIStarter uiStarter, MenuFactory menuFactory, 
		MessageFactory messageFactory, ResourceInjector injector, 
		AddressBookMainView mainView, ContactActions contactActions,
		TaskTestActions taskTestActions, GutsApplicationActions appActions)
	{
		_uiStarter = uiStarter;
		_mainView =  mainView;
		_appActions = appActions;
		_contactActions = contactActions;
		_taskTestActions = taskTestActions;
		_menuFactory = menuFactory;
		_messageFactory = messageFactory;
		_injector = injector;
	}
	//CSON: ParameterNumberCheck
	
	@Override public void startup(String[] args)
	{
		_uiStarter.showUI(createMenuBar(), _mainView);
	}
	
	@Override public void ready()
	{
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

	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(_menuFactory.createMenu("fileMenu", _throwException, _appActions.quit()));
		menuBar.add(_menuFactory.createMenu("editMenu", 
			_appActions.cut(),
			_appActions.copy(),
			_appActions.paste()));
		menuBar.add(_menuFactory.createMenu("contactMenu",
			_contactActions.createContact(),
			_contactActions.createContactWithTabs(),
			_contactActions.createContactWithWizard(),
			MenuFactory.ACTION_SEPARATOR,
			_contactActions.modifyContact(),
			_contactActions.modifyContactWithTabs(),
			_contactActions.modifyContactWithWizard(),
			MenuFactory.ACTION_SEPARATOR,
			_contactActions.deleteContact()));
		menuBar.add(_menuFactory.createMenu("localeMenu", _french, _english));
		menuBar.add(_menuFactory.createMenu("taskTestMenu",
			_taskTestActions._oneTaskNoBlocker,
			_taskTestActions._oneTaskComponentBlocker,
			_taskTestActions._oneTaskActionBlocker,
			_taskTestActions._oneTaskWindowBlocker,
			_taskTestActions._oneTaskDialogBlocker,
			_taskTestActions._oneTaskProgressDialogBlocker,
			MenuFactory.ACTION_SEPARATOR,
			_taskTestActions._oneTaskSerialExecutor,
			_taskTestActions._fiveTasksDialogBlocker,
			_taskTestActions._twoSerialTaskDialogBlocker,
			_taskTestActions._twoSerialGroupsDialogBlocker));
		return menuBar;
	}
	
	// Only to demonstrate the exception handling
	final private GutsAction _throwException = new GutsAction()
	{
		@Override protected void perform()
		{
			throw new IllegalArgumentException("Some message here");
		}
	};
	final private GutsAction _french = new GutsAction()
	{
		@Override protected void perform()
		{
			_injector.setLocale(Locale.FRENCH);
		}
	};
	final private GutsAction _english = new GutsAction()
	{
		@Override protected void perform()
		{
			_injector.setLocale(Locale.ENGLISH);
		}
	};
	
	final private AddressBookUIStarter _uiStarter;
	final private ContactActions _contactActions;
	final private TaskTestActions _taskTestActions;
	final private GutsApplicationActions _appActions;
	final private AddressBookMainView _mainView;
	final private MenuFactory _menuFactory;
	final private MessageFactory _messageFactory;
	final private ResourceInjector _injector;
}
