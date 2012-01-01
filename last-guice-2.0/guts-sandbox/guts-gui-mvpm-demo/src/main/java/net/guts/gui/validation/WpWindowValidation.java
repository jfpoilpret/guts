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

package net.guts.gui.validation;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

import net.guts.gui.action.GutsAction;
import net.guts.gui.action.GutsAction.ObserverPosition;
import net.guts.gui.action.GutsActionAdapter;
import net.guts.gui.action.GutsActionObserver;
import net.guts.gui.template.okcancel.AbortApply;
import net.guts.gui.window.RootPaneConfig;
import net.guts.gui.window.WindowProcessor;

import com.jgoodies.validation.Severity;
import com.jgoodies.validation.Validatable;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;

class WpWindowValidation implements WindowProcessor
{
	@Override public <T extends RootPaneContainer> void process(
		final T root, RootPaneConfig<T> config)
	{
		ValidationConfig validationConfig = config.get(ValidationConfig.class);
		if (validationConfig != null)
		{
			final ValidationResultModel validation = validationConfig._model;
			
			// Require validation when possible
			final Validatable validator = validationConfig._validator;
			if (validator != null)
			{
				final boolean autoFocus = validationConfig._autoFocus;
				
				// Perform early validation if required
				if (validationConfig._validateAtFirstDiplay)
				{
					performValidation(root, false, validator, validation, autoFocus);
				}
				// Perform systematic validation if possible
				GutsAction ok = findButtonAction(root.getContentPane(), "ok");
				GutsAction apply = findButtonAction(root.getContentPane(), "apply");
				if (ok != null || apply != null)
				{
					GutsActionObserver observer = new GutsActionAdapter()
					{
						@Override protected void beforeActionPerform()
						{
							performValidation(root, true, validator, validation, autoFocus);
						}
					};
					addObserver(ok, observer);
					addObserver(apply, observer);
				}
			}
			JComponent view = (JComponent) root.getContentPane();
			JComponent feedback = new IconFeedbackPanel(validation, view);
			root.setContentPane(feedback);
		}
	}
	
	static private void addObserver(GutsAction action, GutsActionObserver observer)
	{
		if (action != null)
		{
			action.addActionObserver(ObserverPosition.FIRST, observer);
		}
	}
	
	static private void performValidation(RootPaneContainer root, boolean abort,
		Validatable validator, ValidationResultModel validation, boolean autoFocus)
	{
		ValidationResult result = validator.validate();
		if (result != null)
		{
			Container contentPane = root.getContentPane();
			validation.setResult(result);
			ValidationComponentUtils.updateComponentTreeSeverity(contentPane, result);
			if (abort && validation.getSeverity() != Severity.OK)
			{
				if (autoFocus)
				{
					focusFirstComponentWithError(contentPane);
				}
				AbortApply.abortApply();
			}
		}
	}
	
	static private void focusFirstComponentWithError(Container pane)
	{
		Container focusRoot = pane.getFocusCycleRootAncestor();
		FocusTraversalPolicy policy = focusRoot.getFocusTraversalPolicy();
		Component component = policy.getDefaultComponent(focusRoot);
		while (component != null)
		{
			if (focusIfError(component))
			{
				break;
			}
			component = policy.getComponentAfter(focusRoot, component);
		}
	}
	
	static private boolean focusIfError(Component component)
	{
		if (!(component instanceof JComponent))
		{
			return false;
		}
		Severity severity = ValidationComponentUtils.getSeverity((JComponent) component);
		if (severity == null || severity == Severity.OK)
		{
			return false;
		}
		component.requestFocus();
		return true;
	}
	
	static private GutsAction findButtonAction(Container container, String name)
	{
		for (Component child: container.getComponents())
		{
			if (child instanceof JButton)
			{
				Action action = ((JButton) child).getAction();
				if (action instanceof GutsAction)
				{
					GutsAction gutsAction = (GutsAction) action;
					if (name.equals(gutsAction.name()))
					{
						return gutsAction;
					}
				}
			}
			else if (child instanceof Container)
			{
				GutsAction action = findButtonAction((Container) child, name);
				if (action != null)
				{
					return action;
				}
			}
		}
		return null;
	}
}
