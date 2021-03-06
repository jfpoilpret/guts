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

package net.guts.demo.addressbook.docking;

import javax.swing.JMenuBar;

import net.guts.demo.addressbook.docking.action.ContactActions;
import net.guts.demo.addressbook.docking.action.GeneralActions;
import net.guts.demo.addressbook.docking.docking.Views;
import net.guts.gui.action.ActionRegistrationManager;
import net.guts.gui.action.GutsAction;
import net.guts.gui.application.GutsApplicationActions;
import net.guts.gui.docking.OpenViewAction;
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
			appActions.quit()));
		add(menuFactory.createMenu("editMenu", 
			appActions.cut(),
			appActions.copy(),
			appActions.paste()));
		add(menuFactory.createMenu("contactMenu",
			contactActions._createContact,
			contactActions._modifyContact,
			contactActions._deleteContact));
		add(menuFactory.createMenu("localeMenu", 
			genActions._french, 
			genActions._english));
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

	static private GutsAction createViewAction(ActionRegistrationManager manager, Views view)
	{
		OpenViewAction action = new OpenViewAction(view.name());
		manager.registerAction(action);
		return action;
	}
}
