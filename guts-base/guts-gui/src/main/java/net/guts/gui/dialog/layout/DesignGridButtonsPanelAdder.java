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

import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;


import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.DesignGridLayoutManager;
import net.java.dev.designgridlayout.INonGridRow;

/**
 * Implementation of {@link net.guts.gui.dialog.layout.ButtonsPanelAdder} 
 * specialized for {@link DesignGridLayout}.
 * 
 * @author Jean-Francois Poilpret
 */
final class DesignGridButtonsPanelAdder implements ButtonsPanelAdder
{
	@Override public void addButtons(JComponent container, List<JButton> buttons)
	{
		DesignGridLayoutManager actualLayout = 
			(DesignGridLayoutManager) container.getLayout();
		DesignGridLayout layout = actualLayout.designGridLayout();
		layout.emptyRow();
		INonGridRow row = layout.row().right();
		for (JButton button: buttons)
		{
			if (button != null)
			{
				row.add(button);
			}
		}
	}
}
