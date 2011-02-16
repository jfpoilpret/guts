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

package net.guts.gui.template.wizard;

import java.awt.CardLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;

import net.guts.gui.action.GutsAction;

public class WizardController
{
	WizardController(JComponent mainView)
	{
		_mainView = mainView;
		_mainView.setLayout(_layout);
		_mainView.addHierarchyListener(new HierarchyListener()
	    {
			@Override public void hierarchyChanged(HierarchyEvent e)
            {
				if (	(e.getID() == HierarchyEvent.HIERARCHY_CHANGED)
					&&	((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0)
					&&	_mainView.isShowing())
				{
					// We must wait until the GUI is visible to show first step
					// of wizard sequence, so that we are sure its enter() method
					// has been called already
					fireStepChanged(-1, 0);
					goToStep(0);
				}
            }
	    });
	}

	public void addWizardListener(WizardListener listener)
	{
		_listeners.add(listener);
	}
	
	public void removeWizardListener(WizardListener listener)
	{
		_listeners.remove(listener);
	}
	
	void addStep(String name, JComponent view, boolean appendToSequence)
	{
		_mainView.add(view, name);
		_steps.put(name, view);
		if (appendToSequence)
		{
			_sequence.add(name);
		}
		if (view instanceof WizardListener)
		{
			addWizardListener((WizardListener) view);
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

	public JComponent getStep(String step)
	{
		return _steps.get(step);
	}
	
	GutsAction nextAction()
	{
		return _next;
	}
	
	GutsAction previousAction()
	{
		return _previous;
	}
	
	void setApplyAction(GutsAction apply)
	{
		_apply = apply;
		_apply.setEnabled(_current == _sequence.size() - 1);
	}

	private void goToStep(int index)
	{
		String step = _sequence.get(index);
		boolean acceptEnabled = (index == _sequence.size() - 1);
		_next.setEnabled(!acceptEnabled);
		_apply.setEnabled(acceptEnabled);
		_previous.setEnabled(index > 0);
		_layout.show(_mainView, step);
	}

	private void fireStepChanged(int oldIndex, int newIndex)
	{
		String oldStep = (oldIndex > -1 ? _sequence.get(oldIndex) : null);
		String newStep = _sequence.get(newIndex);
		for (WizardListener listener: _listeners)
		{
			listener.stepChanged(this, oldStep, newStep);
		}
	}
	
	final private GutsAction _previous = new GutsAction("previous")
	{
		@Override protected void perform()
		{
			if (_current > 0)
			{
				// Return to previous pane
				goToStep(_current - 1);
				fireStepChanged(_current, _current - 1);
				_current--;
			}
		}
	};

	final private GutsAction _next = new GutsAction("next")
	{
		@Override protected void perform()
		{
			if (_current < _sequence.size() - 1)
			{
				goToStep(_current + 1);
				fireStepChanged(_current, _current + 1);
				_current++;
			}
		}
	};
	
	static final private CardLayout _layout = new CardLayout();
	
	final private List<WizardListener> _listeners = new CopyOnWriteArrayList<WizardListener>();
	final private Map<String, JComponent> _steps = new HashMap<String, JComponent>();
	final private JComponent _mainView;
	final private List<String> _sequence = new ArrayList<String>();
	private int _current = 0;
	private GutsAction _apply = null;
}
