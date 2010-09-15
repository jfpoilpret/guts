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
import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import static net.guts.gui.util.LayoutHelper.*;

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
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		int maxVgap = 0;

		// Build the buttons box and calculate all needed sizes
		for (int i = 0; i < buttons.size(); i++)
		{
			JButton left = buttons.get(i);
			// Make sure buttons size will be made consistent during all lifetime of button
			left.addPropertyChangeListener(_sizeListener);
			
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
		
		// Update buttons size
		updateButtonsSize(buttonsPanel, calculateButtonsSize(buttonsPanel));
		
		// Add border around box
		int vgap = bottomGap(container, buttonsPanel);
		buttonsPanel.setBorder(new EmptyBorder(vgap, 0, maxVgap, 0));
		return buttonsPanel;
	}

	static private Dimension calculateButtonsSize(Container panel)
	{
		int width = 0;
		int height = 0;
		for (Component child: panel.getComponents())
		{
			if (child instanceof JButton)
			{
				width = Math.max(width, child.getPreferredSize().width);
				height = Math.max(height, child.getPreferredSize().height);
			}
		}
		return new Dimension(width, height);
	}
	
	static private void updateButtonsSize(Container panel, Dimension size)
	{
		for (Component child: panel.getComponents())
		{
			if (child instanceof JButton)
			{
				if (!size.equals(child.getMinimumSize()))
				{
					child.setMinimumSize(size);
				}
				if (!size.equals(child.getPreferredSize()))
				{
					child.setPreferredSize(size);
				}
			}
		}
	}
	
	static private class ButtonSizeConsistencyHandler implements PropertyChangeListener
	{
		@Override public void propertyChange(PropertyChangeEvent evt)
		{
			if (	!evt.getPropertyName().equals("preferredSize")
				&&	!evt.getPropertyName().equals("minimumSize"))
			{
				Component source = (Component) evt.getSource();
				// First reset default size calculation behavior
				source.setMinimumSize(null);
				source.setPreferredSize(null);
				// Then calculate the max size of all buttons in the parent panel
				Container panel = source.getParent();
				Dimension prefSize = calculateButtonsSize(panel);
				// Now force the size of all buttons
				updateButtonsSize(panel, prefSize);
			}
		}
	}
	
	static final private PropertyChangeListener _sizeListener = 
		new ButtonSizeConsistencyHandler();
}
