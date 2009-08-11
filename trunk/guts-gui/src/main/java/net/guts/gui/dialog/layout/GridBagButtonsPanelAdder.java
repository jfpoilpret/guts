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

package net.guts.gui.dialog.layout;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * Implementation of {@link net.guts.gui.dialog.layout.ButtonsPanelAdder} 
 * specialized for {@link GridBagLayout}.
 * 
 * @author Jean-Francois Poilpret
 */
final class GridBagButtonsPanelAdder extends AbstractButtonsPanelAdder
{
	@Override protected void addButtonsPanel(JComponent container, JPanel commands)
	{
		// Calculate the number of rows and columns
		GridBagLayout layout = (GridBagLayout) container.getLayout();
		Component[] comps = container.getComponents();
		int cols = 0;
		int rows = 0;
		for (Component comp: comps)
		{
			GridBagConstraints constraints = layout.getConstraints(comp);
			cols = Math.max(constraints.gridx + constraints.gridwidth, cols);
			rows = Math.max(constraints.gridy + constraints.gridheight, rows);
		}

		// Add one row
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = rows;
		constraints.gridwidth = cols;
		constraints.gridheight = 1;
		constraints.anchor = GridBagConstraints.LAST_LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		container.add(commands, constraints);
	}
}
