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

package net.guts.gui.template.okcancel;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

//TODO better computation of gaps...
class OkCancelDefaultLayoutAdder implements OkCancelLayoutAdder
{
	@Override public Container layout(
		Container view, JButton ok, JButton cancel, JButton apply)
	{
		// Create a new view that will embed the given view and all added buttons
		JPanel fullView = new JPanel();
		fullView.setLayout(new GridBagLayout());

		// Get only the non null buttons
		List<JButton> buttons = extractRealButtons(ok, cancel, apply);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = buttons.size() + 1;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		fullView.add(view, constraints);

		layoutButtons(fullView, buttons);
		
		return fullView;
	}

	static private List<JButton> extractRealButtons(JButton... buttons)
	{
		List<JButton> realButtons = new ArrayList<JButton>(buttons.length);
		for (JButton button: buttons)
		{
			if (button != null)
			{
				realButtons.add(button);
			}
		}
		return realButtons;
	}
	
	static private void layoutButtons(JPanel pane, List<JButton> buttons)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		// Common for all 4 cells of the common bar
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weighty = 0.0;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(INSET, INSET, INSET, INSET);

		// Add the empty cell on the left (buttons will be right-aligned)
		constraints.gridx = 0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		pane.add(new JPanel(), constraints);
		
		// Add each button then
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		for (JButton button: buttons)
		{
			constraints.gridx++;
			pane.add(button, constraints);
		}
	}
	
	static final private int INSET = 10;
}
