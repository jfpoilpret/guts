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

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

import net.guts.gui.action.AbstractGutsActionObserver;
import net.guts.gui.action.GutsAction;
import net.guts.gui.action.GutsAction.ObserverPosition;
import net.guts.gui.action.GutsActionObserver;
import net.guts.gui.template.okcancel.AbortApply;
import net.guts.gui.window.RootPaneConfig;
import net.guts.gui.window.WindowProcessor;

import com.jgoodies.validation.Validatable;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;

class WpWindowValidation implements WindowProcessor
{
	@Override public <T extends RootPaneContainer> void process(T root, RootPaneConfig<T> config)
	{
		ValidationConfig validationConfig = config.get(ValidationConfig.class);
		if (validationConfig != null)
		{
			final ValidationResultModel validation = validationConfig._model;
			
			// Require validation when possible
			final Validatable validator = validationConfig._validator;
			if (validator != null)
			{
				// Perform early validation (TODO make it configurable????)
				performValidation(false, validator, validation);
				// Perform systematic validation if possible
				GutsAction ok = findButtonAction(root.getContentPane(), "ok");
				GutsAction apply = findButtonAction(root.getContentPane(), "apply");
				if (ok != null || apply != null)
				{
					GutsActionObserver observer = new AbstractGutsActionObserver()
					{
						@Override protected void beforeActionPerform()
						{
							performValidation(true, validator, validation);
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
	
	static private void performValidation(boolean abort,
		Validatable validator, ValidationResultModel validation)
	{
		ValidationResult result = validator.validate();
		if (result != null)
		{
			validation.setResult(result);
			if (abort)
			{
				AbortApply.abortApply();
			}
		}
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
