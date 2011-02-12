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

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import net.guts.gui.action.GutsAction;

//TODO refactor to have direct communication between Decorator and Controller,
// thus avoiding extra calla to Wizard.withNext(), withPrevious()...
public class WizardController
{
	WizardController(JComponent mainView)
	{
		_mainView = mainView;
		_mainView.setLayout(_layout);
	}
	
	void addStep(String name, JComponent view, boolean appendToSequence)
	{
		_mainView.add(view, view.getName());
		_steps.put(name, view);
		if (appendToSequence)
		{
			_sequence.add(name);
		}
	}
	
	public void setNextStepsSequence(String... steps)
	{
		// First check that all steps match added components
		List<String> stepsList = Arrays.asList(steps);
		if (!_steps.keySet().containsAll(stepsList))
		{
			throw new IllegalArgumentException(
				"Some steps have no matching JComponent registered " +
				"(through addWizardPane)");
		}
		// Remove all steps following current step
		if (!_sequence.isEmpty())
		{
			_sequence.retainAll(_sequence.subList(0, _current));
		}
		_sequence.addAll(stepsList);
	}

	public Action getNextAction()
	{
		return _next;
	}
	
	public Action getPreviousAction()
	{
		return _previous;
	}

	private void goToStep(int index)
	{
		String step = _sequence.get(index);
		boolean acceptEnabled = (index == _sequence.size() - 1);
		_next.setEnabled(!acceptEnabled);
		setAcceptEnabled(acceptEnabled);
		_previous.setEnabled(index > 0);
		_layout.show(_mainView, step);
	}
	
	private void setAcceptEnabled(boolean enabled)
	{
		WizardDecorator decorator = (WizardDecorator) SwingUtilities.getAncestorOfClass(
			WizardDecorator.class, _mainView);
		if (decorator != null)
		{
			decorator.applyAction().setEnabled(enabled);
		}
	}

	final private GutsAction _previous = new GutsAction("previous")
	{
		@Override protected void perform()
		{
			if (_current > 0)
			{
				// Return to previous pane
				goToStep(--_current);
			}
		}
	};

	final private GutsAction _next = new GutsAction("next")
	{
		@Override protected void perform()
		{
			if (_current < _sequence.size() - 1)
			{
				goToStep(++_current);
			}
		}
	};
	
	static final private CardLayout _layout = new CardLayout();
	
	final private Map<String, JComponent> _steps = new HashMap<String, JComponent>();
	final private JComponent _mainView;
	final private List<String> _sequence = new ArrayList<String>();
	private int _current = 0;
}
