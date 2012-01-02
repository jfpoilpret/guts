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

import javax.swing.JMenuBar;

import net.guts.gui.action.ActionRegistrationManager;
import net.guts.gui.action.GutsAction;
import net.guts.gui.application.GutsApplicationActions;
import net.guts.gui.docking.OpenViewAction;
import net.guts.gui.examples.addressbook.action.ContactActions;
import net.guts.gui.examples.addressbook.action.GeneralActions;
import net.guts.gui.examples.addressbook.docking.Views;
import net.guts.gui.menu.MenuFactory;

import bibliothek.gui.dock.common.CControl;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AddressBookMenuBar extends JMenuBar
{
	private static final long serialVersionUID = 8815191884342771134L;

	@Inject
	public AddressBookMenuBar(MenuFactory menuFactory, 
		GutsApplicationActions appActions, GeneralActions genActions,
		ContactActions contactActions, ActionRegistrationManager actionManager,
		CControl controller)
	{
		add(menuFactory.createMenu("fileMenu", 
			genActions.throwException(), 
			appActions.quit()));
		add(menuFactory.createMenu("editMenu", 
			appActions.cut(),
			appActions.copy(),
			appActions.paste()));
		add(menuFactory.createMenu("contactMenu",
			contactActions.createContact(),
			contactActions.modifyContact(),
			contactActions.deleteContact()));
		add(menuFactory.createMenu("localeMenu", 
			genActions.frenchLocale(), 
			genActions.englishLocale()));
//		RootMenuPiece viewsListMenu = new RootMenuPiece();
//		viewsListMenu.add(new SingleCDockableListMenuPiece(controller));
//		JMenu views = viewsListMenu.getMenu();
//		views.setName("viewsMenu");
//		add(views);
		add(menuFactory.createMenu("viewsMenu", 
			createViewAction(actionManager, Views.ContactList),
			createViewAction(actionManager, Views.ContactDetail),
			createViewAction(actionManager, Views.ContactPicture)));
	}

	private GutsAction createViewAction(ActionRegistrationManager manager, Views view)
	{
		OpenViewAction action = new OpenViewAction(view.name());
		manager.registerAction(action);
		return action;
	}
}
