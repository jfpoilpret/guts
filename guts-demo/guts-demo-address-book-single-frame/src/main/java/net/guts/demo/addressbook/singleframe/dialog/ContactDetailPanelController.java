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

import net.guts.demo.addressbook.singleframe.business.AddressBookService;
import net.guts.demo.addressbook.singleframe.dialog.ContactDetailPanel;
import net.guts.demo.addressbook.singleframe.domain.Contact;
import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.template.okcancel.OkCancel;
import net.guts.gui.window.JDialogConfig;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class ContactDetailPanelController
{
	public void showContactDialog(Contact contact)
	{
		final boolean isNew = (contact == null);
		final ContactDetailPanel panel = _contactDetail.get();
		panel.modelToView(contact);
		OkCancel template = OkCancel.create().withCancel().withOK(new GutsAction()
		{
			@Override protected void perform()
			{
				if (isNew)
				{
					_service.createContact(panel.viewToModel());
				}
				else
				{
					_service.modifyContact(panel.viewToModel());
				}
			}
		});
		_dialogFactory.showDialog(panel, JDialogConfig.create().merge(template).config());
	}
	
	// Services
	@Inject private DialogFactory _dialogFactory;
	@Inject private AddressBookService _service;
	
	//TODO is a provider really needed here? can't we use just a singleton?
	// Panel providers
	@Inject Provider<ContactDetailPanel> _contactDetail;
}
