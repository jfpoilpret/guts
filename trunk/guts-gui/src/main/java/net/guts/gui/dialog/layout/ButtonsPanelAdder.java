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

/**
 * Implementations of this interface are responsible to add a special area to
 * a dialog panel, dedicated to dialog buttons (such as "OK", "Cancel", 
 * "Apply"...) This area must appear at the bottom of the panel.
 * <p/>
 * There is one implementation per supported {@link java.awt.LayoutManager} so 
 * that buttons can be added directly to the panel without having to create an
 * embedded panel for holding the buttons.
 * <p/>
 * Use {@link ButtonsPanelAdderFactory} to get the right implementation for a
 * given {@link java.awt.LayoutManager}.
 * 
 * @author Jean-Francois Poilpret
 */
public interface ButtonsPanelAdder
{
	/**
	 * The vertical gap between the dialog panel contents (labels, fields...)
	 * and the buttons area.
	 */
	static public final int	VGAP = 10;

	/**
	 * Adds the passed {@code buttons} to the given {@code container} in an area
	 * located at the bottom of {@code container}.
	 * @param container the panel to which {@code buttons} must be added
	 * @param buttons the buttons to add to {@code container}
	 */
	public void	addButtons(JComponent container, List<JButton> buttons);
}
