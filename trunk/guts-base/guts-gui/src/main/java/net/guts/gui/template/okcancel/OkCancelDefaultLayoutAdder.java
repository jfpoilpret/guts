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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import static net.guts.gui.util.LayoutHelper.bottomGap;
import static net.guts.gui.util.LayoutHelper.leftGap;
import static net.guts.gui.util.LayoutHelper.relatedHorizontalGap;
import static net.guts.gui.util.LayoutHelper.rightGap;
import static net.guts.gui.util.LayoutHelper.topGap;

class OkCancelDefaultLayoutAdder implements OkCancelLayoutAdder
{
	@Override public Container layout(
		Container view, JButton ok, JButton cancel, JButton apply)
	{
		// Create a new view that will embed the given view and all added buttons
		JPanel fullView = new JPanel();
		fullView.setLayout(new BorderLayout());

		// Add view to the center of the panel
		fullView.add(view, BorderLayout.CENTER);
		
		// Create a panel with all buttons and add it at the bottom
		JPanel buttons = createButtonsPanel(extractRealButtons(ok, cancel, apply), fullView);
		fullView.add(buttons, BorderLayout.SOUTH);

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
	
	static private void setupButtonsSize(List<JButton> buttons)
	{
		int width = 0;
		int height = 0;
		for (JButton button: buttons)
		{
			width = Math.max(width, button.getPreferredSize().width);
			height = Math.max(height, button.getPreferredSize().height);
		}
		Dimension size = new Dimension(width, height);
		for (JButton button: buttons)
		{
			button.setMinimumSize(size);
			button.setPreferredSize(size);
			button.setMaximumSize(size);
		}
	}
	
	static private JPanel createButtonsPanel(List<JButton> buttons, JPanel container)
	{
		// Make all sizes consistent
		setupButtonsSize(buttons);

		// Create panel that will hold all buttons
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		int maxVgap = 0;

		// Build the buttons box and calculate all needed sizes
		for (int i = 0; i < buttons.size(); i++)
		{
			JButton left = buttons.get(i);
			int vgap = topGap(buttonsPanel, left);
			maxVgap = Math.max(maxVgap, vgap);
			if (i == 0)
			{
				// Add left gutter
				int hgap = leftGap(buttonsPanel, left);
				buttonsPanel.add(Box.createHorizontalStrut(hgap));
			}
			buttonsPanel.add(left);
			if (i != buttons.size() - 1)
			{
				// Add inter-buttons gutter
				JButton right = buttons.get(i + 1);
				int gap = relatedHorizontalGap(buttonsPanel, left, right);
				buttonsPanel.add(Box.createHorizontalStrut(gap));
			}
			else
			{
				// Add right gutter
				int hgap = rightGap(buttonsPanel, left);
				buttonsPanel.add(Box.createHorizontalStrut(hgap));
			}
		}
		
		// Add border around box
		int vgap = bottomGap(container, buttonsPanel);
		buttonsPanel.setBorder(new EmptyBorder(vgap, 0, maxVgap, 0));
		return buttonsPanel;
	}
}
