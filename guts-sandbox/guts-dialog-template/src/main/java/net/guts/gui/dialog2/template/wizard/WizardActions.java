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

import javax.swing.Action;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO is this class useful really? normally all actions should be mandatory in WizardConfig!
final public class WizardActions
{
	static final private Logger _logger = LoggerFactory.getLogger(WizardActions.class);
	
	static public WizardActions forRootPane(JRootPane root)
	{
		Container content = root.getContentPane();
		if (content instanceof WizardDecorator)
		{
			return new WizardActions(((WizardDecorator) content));
		}
		else
		{
			_logger.warn(
				"forRootPane() root content isn't a WizardDecorator! root = {}", root);
			return null;
		}
	}
	
	static public WizardActions forRootPaneContainer(RootPaneContainer container)
	{
		return forRootPane(container.getRootPane());
	}
	
	static public WizardActions forView(Container view)
	{
		return forRootPane(SwingUtilities.getRootPane(view));
	}

	private WizardActions(WizardDecorator wizardPane)
	{
		_wizardPane = wizardPane;
	}
	
	public Action getPreviousAction()
	{
		return _wizardPane.previousAction();
	}

	public Action getNextAction()
	{
		return _wizardPane.nextAction();
	}

	public Action getCancelAction()
	{
		return _wizardPane.cancelAction();
	}

	public Action getApplyAction()
	{
		return _wizardPane.applyAction();
	}

	final private WizardDecorator _wizardPane;
}
