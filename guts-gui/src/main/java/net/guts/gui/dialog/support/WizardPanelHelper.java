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

package net.guts.gui.dialog.support;

import java.awt.CardLayout;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.java.dev.designgridlayout.DesignGridLayout;

// Helper for AbstractWizardPanel. It is responsible of all components shown
// by AbstractWizardPanel (description label, pane showing all steps subpanes
// (one at a time)).
final class WizardPanelHelper
{
	WizardPanelHelper(String id)
	{
	    _wizard.setName(id + MAINPANE_NAME_SUFFIX);
	}

	void initLayout(JComponent parent)
    {
		DesignGridLayout layout = new DesignGridLayout(parent);
		layout.row().left().fill().add(_stepDescription);
		layout.emptyRow();
		layout.row().left().fill().add(new JSeparator());
		layout.row().grid().add(_wizard);
		layout.row().left().fill().add(new JSeparator());
    }

	void addStep(JComponent pane)
	{
		_wizard.add(pane, pane.getName());
		JLabel description = new JLabel();
	    description.setName(pane.getName() + STEP_LABEL_NAME_SUFFIX);
	    description.setFont(_font);
		_stepDescription.add(description, pane.getName());
	}
	
	void showStep(String step)
	{
		_descriptionLayout.show(_stepDescription, step);
		_wizardLayout.show(_wizard, step);
	}
	
	void reset()
	{
		_wizard.removeAll();
		_stepDescription.removeAll();
	}

	static final private String MAINPANE_NAME_SUFFIX = "-wizards";
	static final private String STEP_LABEL_NAME_SUFFIX = "-label";
	static final private Font _font = Font.decode("dialog-BOLD-18");
	private final CardLayout _wizardLayout = new CardLayout();
	private final JPanel _wizard = new JPanel(_wizardLayout);
	private final CardLayout _descriptionLayout = new CardLayout();
	private final JPanel _stepDescription = new JPanel(_descriptionLayout);
}
