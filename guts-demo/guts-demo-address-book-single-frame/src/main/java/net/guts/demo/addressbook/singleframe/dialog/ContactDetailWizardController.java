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

package net.guts.demo.addressbook.singleframe.dialog;

import javax.swing.JComponent;

import net.guts.demo.addressbook.singleframe.business.AddressBookService;
import net.guts.demo.addressbook.singleframe.domain.Contact;
import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.template.wizard.Wizard;
import net.guts.gui.window.JDialogConfig;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContactDetailWizardController
{
	public void showContactWizard(final Contact contact, final boolean isNew)
	{
		// Instantiate each step panel, setup their models
		_contactStep.modelToView(contact);
		_homeAddressStep.modelToView(contact);
		_officeAddressStep.modelToView(contact);
		// Prepare wizard config
		Wizard wizard = Wizard.create()
			.mapNextStep(_contactStep)
			.mapNextStep(_homeAddressStep)
			.mapNextStep(_officeAddressStep)
			.withFinish(new GutsAction()
			{
				@Override protected void perform()
				{
					_contactStep.accept();
					_homeAddressStep.accept();
					_officeAddressStep.accept();
					if (isNew)
					{
						_service.createContact(contact);
					}
					else
					{
						_service.modifyContact(contact);
					}
				}
			});
		JComponent mainView = wizard.mainView();
		mainView.setName("ContactDetailWizardPanel");
		_dialogFactory.showDialog(mainView, JDialogConfig.create().merge(wizard).config());
	}

	@Inject private DialogFactory _dialogFactory;
	@Inject private AddressBookService _service;
	@Inject ContactStep _contactStep;
	@Inject HomeAddressStep _homeAddressStep;
	@Inject OfficeAddressStep _officeAddressStep;
}
