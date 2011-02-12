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

package net.guts.gui.dialog2.wizard;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;

import net.guts.gui.action.GutsAction;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

public class WizardControllerImpl implements WizardController
{
	@Inject WizardControllerImpl(@Assisted JComponent mainView, Injector injector)
	{
		_mainView = mainView;
		_mainView.setLayout(_layout);
		_injector = injector;
	}
	
	@Override public void addWizardPane(JComponent pane, boolean appendToSequence)
    {
		_mainView.add(pane, pane.getName());
		_panes.put(pane.getName(), pane);
		if (appendToSequence)
		{
			_sequence.add(pane.getName());
		}
    }

	@Override 
	public void addWizardPane(Class<? extends JComponent> pane, boolean appendToSequence)
	{
		addWizardPane(_injector.getInstance(pane), appendToSequence);
	}
	
	@Override public void setNextStepsSequence(String... steps)
    {
		// First check that all steps match added components
		List<String> stepsList = Arrays.asList(steps);
		if (!_panes.keySet().containsAll(stepsList))
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

	@Override public Action getNextAction()
	{
		return _next;
	}
	
	@Override public Action getPreviousAction()
	{
		return _previous;
	}

	private void goToStep(int index)
	{
		String step = _sequence.get(index);
		boolean acceptEnabled = (index == _sequence.size() - 1);
		_next.setEnabled(!acceptEnabled);
//		_controller.setAcceptEnabled(acceptEnabled);
		_previous.setEnabled(index > 0);
		_layout.show(_mainView, step);
	}

	private final GutsAction _previous = new GutsAction("previous")
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

	private final GutsAction _next = new GutsAction("next")
	{
		@Override protected void perform()
		{
			if (_current < _sequence.size() - 1)
			{
				goToStep(++_current);
			}
		}
	};

	private final Injector _injector;
	private final JComponent _mainView;
	private final CardLayout _layout = new CardLayout();
	private final Map<String, JComponent> _panes =  new HashMap<String, JComponent>();
	private final List<String> _sequence = new ArrayList<String>();
	private int _current = 0;
}