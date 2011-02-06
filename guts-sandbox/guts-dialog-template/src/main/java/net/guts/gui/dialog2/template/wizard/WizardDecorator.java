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

package net.guts.gui.dialog2.template.wizard;

import java.awt.Container;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.RootPaneContainer;

import net.guts.gui.action.ActionRegistrationManager;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.GutsActionDecorator;
import net.guts.gui.dialog2.template.TemplateDecorator;
import net.guts.gui.dialog2.template.util.TemplateHelper;
import net.guts.gui.dialog2.template.wizard.Wizard.Result;
import net.guts.gui.window.RootPaneConfig;
import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.Tag;

import com.google.inject.Inject;

class WizardDecorator extends JPanel implements TemplateDecorator
{
	private static final long serialVersionUID = -3076781500914299784L;

	@Inject WizardDecorator(ActionRegistrationManager actionRegistry)
	{
		_actionRegistry = actionRegistry;
	}

	@Override public <T extends RootPaneContainer> void decorate(
		T container, Container view, RootPaneConfig<T> configuration)
	{
		setName(view.getName() + "-wizard");
		_stepDescription.setName(view.getName() + "-label");
		_stepDescription.setFont(_font);

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
		
		// Setup initial step
		_model = config._model;
		goToStep(_model.getInitialStep());
		
		// Add view to container
		container.setContentPane(this);
	}
	
	private void goToStep(String step)
	{
		_stepDescription.setText(_model.getStepDescription(step));
		_model.setCurrentStep(step);
		_previous.setEnabled(_model.getPreviousStep() != null);
		_next.setEnabled(_model.getNextStep() != null);
		_ok.setEnabled(_model.getNextStep() == null);
	}
	
	private void installActions(RootPaneContainer container, WizardConfig config)
	{
		// Create the right actions when needed
//		_previous.setAction(setupAction(createPreviousAction()));
//		_next.setAction(setupAction(createNextAction()));
		_ok.setAction(setupAction(
			createClosingAction("ok", config._apply, config, Result.OK)));
		_cancel.setAction(setupAction(
			createClosingAction("cancel", config._cancel, config, Result.CANCEL)));
	}

	private GutsAction createClosingAction(String name, Action action,
		final WizardConfig config, final Wizard.Result result)
	{
		final RootPaneContainer container = (RootPaneContainer) getRootPane().getParent();
		if (action != null)
		{
			return new GutsActionDecorator(name, action)
			{
				@Override protected void afterTargetPerform()
				{
					config._result = result;
					TemplateHelper.close(container);
				}
			};
		}
		else
		{
			return new GutsAction(name)
			{
				@Override protected void perform()
				{
					config._result = result;
					TemplateHelper.close(container);
				}
			};
		}
	}
	
	private GutsAction setupAction(GutsAction action)
	{
		_actionRegistry.registerAction(action);
		return action;
	}
	
	Action previousAction()
	{
		return _previousAction;
	}
	
	Action nextAction()
	{
		return _previousAction;
	}
	
	Action cancelAction()
	{
		return _cancel.getAction();
	}
	
	Action applyAction()
	{
		return _ok.getAction();
	}
	
	static final private Font _font = Font.decode("dialog-BOLD-18");

	final private GutsAction _previousAction = new GutsAction("previous")
	{
		@Override protected void perform()
		{
			goToStep(_model.getPreviousStep());
		}
	};
	
	final private GutsAction _nextAction = new GutsAction("next")
	{
		@Override protected void perform()
		{
			goToStep(_model.getNextStep());
		}
	};
	
	final private ActionRegistrationManager _actionRegistry;
	private final JLabel _stepDescription = new JLabel();
	private final JButton _ok = new JButton();
	private final JButton _cancel = new JButton();
	private final JButton _previous = new JButton(_previousAction);
	private final JButton _next = new JButton(_nextAction);
	private WizardModel _model = null;
}
