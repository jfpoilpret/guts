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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MutableWizardModel extends AbstractWizardModel
{
	public void setNextStepsSequence(String... steps)
    {
		// First check that all steps match added components
		List<String> stepsList = Arrays.asList(steps);
		// Remove all steps following current step
		if (_current > -1 && !_sequence.isEmpty())
		{
			_sequence.retainAll(_sequence.subList(0, _current));
		}
		_sequence.addAll(stepsList);
		fireModelChanged();
    }
	
	@Override public String getInitialStep()
	{
		return (_sequence.isEmpty() ? null : _sequence.get(0));
	}
	
	@Override public String getCurrentStep()
	{
		return (_current > -1 ? _sequence.get(_current) : null);
	}
	
	@Override public String getPreviousStep()
	{
		return (_current > 0 ? _sequence.get(_current - 1) : null);
	}
	
	@Override public String getNextStep()
	{
		return (_current < _sequence.size() - 1 ? _sequence.get(_current + 1) : null);
	}
	
	@Override public String getStepDescription(String step)
	{
		return step;
	}
	
	@Override public void setCurrentStep(String newStep)
	{
		String oldStep = getCurrentStep();
		int index = 0;
		for (String step: _sequence)
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

	private final List<String> _sequence = new ArrayList<String>();
	private int _current = -1;
}
