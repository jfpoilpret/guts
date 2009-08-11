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

import javax.swing.JPanel;

import org.jdesktop.application.Task;

public abstract class AbstractWizardStepPanel extends JPanel implements WizardStepPanel
{
	public void enter()
	{
	}

	public <T, V> Task<T, V> leave()
	{
		return null;
	}

	final public void setController(WizardController controller)
	{
		_controller = controller;
	}
	
	final protected WizardController getController()
	{
		return _controller;
	}

	private static final long serialVersionUID = -5998189407857391710L;

	private WizardController _controller;
}
