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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;

public class DemoWizardStep3 extends JPanel
{
	public DemoWizardStep3()
	{
		DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().grid(_label).add(_field);
	}

	final private JLabel _label = new JLabel("Some label");
	final private JTextField _field = new JTextField(10);
}
