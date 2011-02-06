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

final public class WizardController
{
	static final private Logger _logger = LoggerFactory.getLogger(WizardController.class);
	
	static public WizardController forRootPane(JRootPane root)
	{
		Container content = root.getContentPane();
		if (content instanceof WizardDecorator)
		{
			return new WizardController(((WizardDecorator) content));
		}
		else
		{
			_logger.warn(
				"forRootPane() root content isn't a WizardDecorator! root = {}", root);
			return null;
		}
	}
	
	static public WizardController forRootPaneContainer(RootPaneContainer container)
	{
		return forRootPane(container.getRootPane());
	}
	
	static public WizardController forView(Container view)
	{
		return forRootPane(SwingUtilities.getRootPane(view));
	}

	private WizardController(WizardDecorator wizardPane)
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
