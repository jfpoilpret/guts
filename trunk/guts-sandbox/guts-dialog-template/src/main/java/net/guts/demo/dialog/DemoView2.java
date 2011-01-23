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

package net.guts.demo.dialog;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class DemoView2 extends JPanel
{
	public DemoView2()
	{
		MigLayout layout = new MigLayout("wrap 4", "[][grow,fill]unrel[][grow,fill]");
		setLayout(layout);
		add(_lblFirstName, "");
		add(_txfFirstName, "span");
		add(_lblLastName);
		add(_txfLastName, "span");
		add(_lblBirth);
		add(_txfBirth, "span, wrap unrel");

		add(_header_name, "");
		add(_header_ruler, "span");
		add(_lblStreet1, "gap indent");
		add(_txfStreet1, "span");
		add(_lblStreet2, "gap indent");
		add(_txfStreet2, "span");
		add(_lblZip, "gap indent");
		add(_txfZip);
		add(_lblCity);
		add(_txfCity);
		add(_lblPhone, "gap indent");
		add(_txfPhone, "span");
	}
	
	final private JLabel _lblFirstName = new JLabel();
	final private JTextField _txfFirstName = new JTextField(20);
	final private JLabel _lblLastName = new JLabel();
	final private JTextField _txfLastName = new JTextField(20);
	final private JLabel _lblBirth = new JLabel();
	final private JFormattedTextField _txfBirth = new JFormattedTextField();
	final private JLabel _header_name = new JLabel();
	final private JSeparator _header_ruler = new JSeparator();
	final private JLabel _lblStreet1 = new JLabel();
	final private JTextField _txfStreet1 = new JTextField(32);
	final private JLabel _lblStreet2 = new JLabel();
	final private JTextField _txfStreet2 = new JTextField(32);
	final private JLabel _lblZip = new JLabel();
	final private JTextField _txfZip = new JTextField(8);
	final private JLabel _lblCity = new JLabel();
	final private JTextField _txfCity = new JTextField(20);
	final private JLabel _lblPhone = new JLabel();
	final private JTextField _txfPhone = new JTextField(10);
}
