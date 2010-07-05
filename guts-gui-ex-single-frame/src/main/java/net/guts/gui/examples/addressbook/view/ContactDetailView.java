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

package net.guts.gui.examples.addressbook.view;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.guts.event.Consumes;
import net.guts.gui.examples.addressbook.domain.Contact;
import net.java.dev.designgridlayout.DesignGridLayout;

import com.google.inject.Singleton;

@Singleton
public class ContactDetailView extends JPanel
{
	static final private long serialVersionUID = -1436540113538430985L;
	static final private double BORDER = 0.5;
	
	public ContactDetailView()
	{
		// Widgets setup
		_txfFirstName.setEditable(false);
		_txfLastName.setEditable(false);
		_txfBirth.setEditable(false);
		
		// Layout the view
		DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().grid(_lblFirstName).add(_txfFirstName);
		layout.row().grid(_lblLastName).add(_txfLastName);
		layout.row().grid(_lblBirth).add(_txfBirth);
		layout.margins(BORDER);
	}

	@Consumes public void onEvent(Contact selected)
	{
		if (selected != null)
		{
			_txfFirstName.setText(selected.getFirstName());
			_txfLastName.setText(selected.getLastName());
			_txfBirth.setValue(selected.getBirth());
		}
		else
		{
			_txfFirstName.setText("");
			_txfLastName.setText("");
			_txfBirth.setValue(null);
		}
	}
	
	final private JLabel _lblFirstName = new JLabel();
	final private JTextField _txfFirstName = new JTextField();
	final private JLabel _lblLastName = new JLabel();
	final private JTextField _txfLastName = new JTextField();
	final private JLabel _lblBirth = new JLabel();
	final private JFormattedTextField _txfBirth = new JFormattedTextField();
}
