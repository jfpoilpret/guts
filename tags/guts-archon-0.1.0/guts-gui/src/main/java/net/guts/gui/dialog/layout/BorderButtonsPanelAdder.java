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

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;


/**
 * Implementation of {@link net.guts.gui.dialog.layout.ButtonsPanelAdder} 
 * specialized for {@link BorderLayout}.
 * 
 * @author Jean-Francois Poilpret
 */
final class BorderButtonsPanelAdder extends AbstractButtonsPanelAdder
{
	@Override protected void addButtonsPanel(JComponent container, JPanel commands)
	{
		container.add(commands, BorderLayout.SOUTH);
	}
}
