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

package net.guts.gui.addressbook.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton public class AddressBookMainView extends JPanel
{
	private static final long serialVersionUID = 597861979604528572L;

	@Inject public AddressBookMainView(ContactsListView list, 
		ContactDetailView detail, ContactPictureView picture)
	{
		// Build the layout now
		setLayout(new BorderLayout());
		JSplitPane secondarySplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		secondarySplit.setTopComponent(list);
		secondarySplit.setBottomComponent(detail);
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainSplit.setLeftComponent(secondarySplit);
		mainSplit.setRightComponent(picture);
		add(mainSplit);
	}
}
