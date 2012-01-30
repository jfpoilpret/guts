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

package net.guts.demo.addressbook.docking.dialog;

import net.guts.demo.addressbook.docking.business.AddressBookService;
import net.guts.demo.addressbook.docking.domain.Contact;
import net.guts.gui.action.GutsAction;
import net.guts.gui.dialog.DialogFactory;
import net.guts.gui.template.okcancel.OkCancel;
import net.guts.gui.window.JDialogConfig;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ContactDetailPanelController
{
	public void showContactDialog(Contact contact)
	{
		final boolean isNew = (contact == null);
		_contactDetail.modelToView(contact);
		OkCancel template = OkCancel.create().withCancel().withOK(new GutsAction()
		{
			@Override protected void perform()
			{
				if (isNew)
				{
					_service.createContact(_contactDetail.viewToModel());
				}
				else
				{
					_service.modifyContact(_contactDetail.viewToModel());
				}
			}
		});
		_dialogFactory.showDialog(
			_contactDetail, JDialogConfig.create().merge(template).config());
	}
	
	@Inject private DialogFactory _dialogFactory;
	@Inject private AddressBookService _service;
	@Inject ContactDetailPanel _contactDetail;
}
