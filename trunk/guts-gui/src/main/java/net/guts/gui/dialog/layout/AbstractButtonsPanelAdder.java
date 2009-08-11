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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jdesktop.layout.LayoutStyle;

/**
 * An abstract implementation of {@link ButtonsPanelAdder} that creates a 
 * {@link JPanel} that holds all buttons and lets concrete subclasses add it
 * to the dialog panel.
 * 
 * @author Jean-Francois Poilpret
 */
public abstract class AbstractButtonsPanelAdder implements ButtonsPanelAdder
{
	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.support.ButtonsPanelAdder#addButtons(javax.swing.JComponent, java.util.List)
	 */
	final public void addButtons(JComponent container, List<JButton> buttons)
	{
		List<JButton> actualButtons = new ArrayList<JButton>();
		for (JButton button: buttons)
		{
			if (button != null)
			{
				actualButtons.add(button);
			}
		}
		JPanel buttonsPanel = createButtonsPanel(actualButtons, container);
		addButtonsPanel(container, buttonsPanel);
	}
	
	/**
	 * Adds the {@link JPanel} containing all buttons to {@code container}. 
	 * Concrete subclasses must implement this method.
	 * 
	 * @param container the panel to which {@code buttons} must be added
	 * @param buttonsPanel the panel of buttons to add to {@code container}
	 */
	abstract protected void addButtonsPanel(JComponent container, JPanel buttonsPanel);

	static private JPanel createButtonsPanel(List<JButton> buttons, JComponent container)
	{
		LayoutStyle style = LayoutStyle.getSharedInstance();
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		int maxVgap = 0;
		int width = 0;
		int height = 0;

		// Build the buttons box and calculate all needed sizes
		for (int i = 0; i < buttons.size(); i++)
		{
			JButton left = buttons.get(i);
			width = Math.max(width, left.getPreferredSize().width);
			height = Math.max(height, left.getPreferredSize().height);
			int vgap = style.getContainerGap(left, SwingConstants.SOUTH, buttonsPanel);
			maxVgap = Math.max(maxVgap, vgap);
			if (i == 0)
			{
				// Add left gutter
				int hgap = style.getContainerGap(left, SwingConstants.WEST, buttonsPanel);
				buttonsPanel.add(Box.createHorizontalStrut(hgap));
			}
			buttonsPanel.add(left);
			if (i != buttons.size() - 1)
			{
				// Add inter-buttons gutter
				JButton right = buttons.get(i + 1);
				int gap = style.getPreferredGap(left,
												right, 
												LayoutStyle.RELATED, 
												SwingConstants.EAST, 
												buttonsPanel);
				buttonsPanel.add(Box.createHorizontalStrut(gap));
			}
			else
			{
				// Add right gutter
				int hgap = style.getContainerGap(left, SwingConstants.EAST, buttonsPanel);
				buttonsPanel.add(Box.createHorizontalStrut(hgap));
			}
		}
		// Resize every button to its preferred size
		Dimension prefSize = new Dimension(width, height);
		for (JButton button: buttons)
		{
			button.setMinimumSize(prefSize);
			button.setPreferredSize(prefSize);
		}
		// Add border around box
		int vgap = style.getContainerGap(buttonsPanel, SwingConstants.SOUTH, container);
		buttonsPanel.setBorder(new EmptyBorder(vgap, 0, maxVgap, 0));
		return buttonsPanel;
	}
}
