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

package net.guts.gui.template.wizard;

import java.awt.Container;
import java.awt.Font;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.RootPaneContainer;

import net.guts.gui.action.ActionRegistrationManager;
import net.guts.gui.action.GutsAction;
import net.guts.gui.template.TemplateDecorator;
import net.guts.gui.template.TemplateHelper;
import net.guts.gui.window.RootPaneConfig;
import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.Tag;

import com.google.inject.Inject;

class WizardDecorator extends JPanel implements TemplateDecorator
{
	private static final long serialVersionUID = -3076781500914299784L;

	@Inject 
	WizardDecorator(ActionRegistrationManager actionRegistry, WizardStepDescriptionFont font)
	{
		_actionRegistry = actionRegistry;
		_font = font.getFont();
	}

	@Override public <T extends RootPaneContainer> void decorate(
		T container, Container view, RootPaneConfig<T> configuration)
	{
		setName(view.getName() + "-wizard");
		_cancel.setName(view.getName() + "-cancel");
		_ok.setName(view.getName() + "-ok");
		_previous.setName(view.getName() + "-previous");
		_next.setName(view.getName() + "-next");
		_stepDescription.setName(view.getName() + "-label");
		_stepDescription.setFont(_font);
		// The following is required  in order to force the correct height for this label
		_stepDescription.setText(" ");

		// Get config passed by the initial caller
		WizardConfig config = configuration.get(WizardConfig.class);

		DesignGridLayout layout = new DesignGridLayout(this);
		layout.row().left().fill().add(_stepDescription);
		layout.emptyRow();
		layout.row().left().fill().add(new JSeparator());
		layout.row().grid().add((JComponent) view);
		layout.row().left().fill().add(new JSeparator());
		layout.row().bar().add(_cancel, Tag.CANCEL).add(_ok, Tag.FINISH)
			.add(_previous, Tag.PREVIOUS).add(_next, Tag.NEXT);

		// Create necessary Actions
		installActions(container, config);
		
		// Add view to container
		container.setContentPane(this);
	}
	
	private void installActions(RootPaneContainer container, WizardConfig config)
	{
		WizardController controller = config._controller;
		// Create the right actions when needed
		_previous.setAction(setupAction(controller.previousAction()));
		_next.setAction(setupAction(controller.nextAction()));
		WizardActions actions = new WizardActions(container, config);
		_actionRegistry.registerActions(actions);
		_cancel.setAction(actions._cancel);
		_ok.setAction(actions._ok);
		controller.setApplyAction(actions._ok);
		
		controller.addWizardListener(new WizardListener()
		{
			@Override public void stepChanged(
				WizardController controller, String oldStep, String newStep)
			{
				// _stepDescriptions may be null (ie not injected due to lacking property)
				if (_stepDescriptions != null)
				{
					_stepDescription.setText(_stepDescriptions.get(newStep));
				}
			}
		});
		
		container.getRootPane().setDefaultButton(_ok);
		TemplateHelper.mapEscapeToCancel(container, _cancel.getAction());
	}

	private GutsAction setupAction(GutsAction action)
	{
		if (action != null)
		{
			_actionRegistry.registerAction(action);
		}
		return action;
	}
	
	Action previousAction()
	{
		return _previous.getAction();
	}
	
	Action nextAction()
	{
		return _next.getAction();
	}
	
	Action cancelAction()
	{
		return _cancel.getAction();
	}
	
	Action applyAction()
	{
		return _ok.getAction();
	}
	
	final private Font _font;
	final private ActionRegistrationManager _actionRegistry;
	private final JLabel _stepDescription = new JLabel();
	private final JButton _ok = new JButton();
	private final JButton _cancel = new JButton();
	private final JButton _previous = new JButton();
	private final JButton _next = new JButton();
	private Map<String, String> _stepDescriptions;
}
