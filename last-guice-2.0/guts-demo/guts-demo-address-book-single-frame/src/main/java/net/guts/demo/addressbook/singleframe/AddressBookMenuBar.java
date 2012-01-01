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

import javax.swing.JMenuBar;

import net.guts.demo.addressbook.singleframe.action.ContactActions;
import net.guts.demo.addressbook.singleframe.action.GeneralActions;
import net.guts.demo.addressbook.singleframe.action.TaskTestActions;
import net.guts.gui.application.GutsApplicationActions;
import net.guts.gui.menu.MenuFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AddressBookMenuBar extends JMenuBar
{
	private static final long serialVersionUID = 8815191884342771134L;

	@Inject
	public AddressBookMenuBar(MenuFactory menuFactory, 
		GutsApplicationActions appActions, GeneralActions genActions,
		ContactActions contactActions, TaskTestActions taskTestActions)
	{
		add(menuFactory.createMenu("fileMenu", 
			genActions.throwException(), 
//			genActions.showPreferences(),
			appActions.quit()));
		add(menuFactory.createMenu("editMenu", 
			appActions.cut(),
			appActions.copy(),
			appActions.paste()));
		add(menuFactory.createMenu("contactMenu",
			contactActions.createContact(),
			contactActions.createContactWithTabs(),
			contactActions.createContactWithWizard(),
			MenuFactory.ACTION_SEPARATOR,
			contactActions.modifyContact(),
			contactActions.modifyContactWithTabs(),
			contactActions.modifyContactWithWizard(),
			MenuFactory.ACTION_SEPARATOR,
			contactActions.deleteContact()));
		add(menuFactory.createMenu("localeMenu", 
			genActions.frenchLocale(), 
			genActions.englishLocale()));
		add(menuFactory.createMenu("taskTestMenu",
			taskTestActions._oneTaskNoBlocker,
			taskTestActions._oneTaskComponentBlocker,
			taskTestActions._oneTaskActionBlocker,
			taskTestActions._oneTaskWindowBlocker,
			taskTestActions._oneTaskDialogBlocker,
			taskTestActions._oneTaskProgressDialogBlocker,
			MenuFactory.ACTION_SEPARATOR,
			taskTestActions._oneTaskSerialExecutor,
			taskTestActions._fiveTasksDialogBlocker,
			taskTestActions._twoSerialTaskDialogBlocker,
			taskTestActions._twoSerialGroupsDialogBlocker));
	}
}
