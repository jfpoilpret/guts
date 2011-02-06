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

public class StaticWizardModel extends AbstractWizardModel
{
	public StaticWizardModel(String... steps)
	{
		_steps = steps;
		_current = -1;
	}

	@Override public String getCurrentStep()
	{
		return (_current > -1 ? _steps[_current] : null);
	}

	@Override public String getInitialStep()
	{
		return _steps[0];
	}
	
	@Override public String getPreviousStep()
	{
		return (_current > 0 ? _steps[_current - 1] : null);
	}

	@Override public String getNextStep()
	{
		return (_current < _steps.length - 1 ? _steps[_current + 1] : null);
	}

	@Override public String getStepDescription(String step)
	{
		//TODO how tom improve? Resource Injection?
		return step;
	}

	@Override public void setCurrentStep(String newStep)
	{
		String oldStep = getCurrentStep();
		int index = 0;
		for (String step: _steps)
		{
			if (step.equals(newStep))
			{
				_current = index;
				fireStepChanged(oldStep, newStep);
				return;
			}
			index++;
		}
		//TODO log something when newStep is unknown?
	}	

	final private String[] _steps;
	private int _current;
}
