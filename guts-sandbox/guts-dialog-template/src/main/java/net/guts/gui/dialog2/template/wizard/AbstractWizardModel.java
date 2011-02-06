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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractWizardModel implements WizardModel
{
	@Override final public void addWizardModelListener(WizardModelListener listener)
	{
		_listeners.add(listener);
	}
	
	@Override final public void removeWizardModelListener(WizardModelListener listener)
	{
		_listeners.remove(listener);
	}

	final protected void fireStepChanged(String oldStep, String newStep)
	{
		for (WizardModelListener listener: _listeners)
		{
			listener.stepChanged(this, oldStep, newStep);
		}
	}

	final protected void fireModelChanged()
	{
		for (WizardModelListener listener: _listeners)
		{
			listener.modelChanged(this);
		}
	}

	final private List<WizardModelListener> _listeners = 
		new CopyOnWriteArrayList<WizardModelListener>();
}
