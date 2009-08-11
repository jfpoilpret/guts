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

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;

import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

//TODO better exception handling and/or logging on error conditions
abstract public class AbstractWizardPanel extends AbstractMultiPanel
{
	/**
	 * Constructs a new abstract wizard panel, with a unique identifier, used 
	 * for resources internationalization.
	 * 
	 * @param id unique identifier for this dialog panel
	 */
	protected AbstractWizardPanel(String id)
    {
	    super(id);
		_mainPane = new WizardPanelHelper(id);
	    addHierarchyListener(new HierarchyListener()
	    {
			public void hierarchyChanged(HierarchyEvent e)
            {
				if (	(e.getID() == HierarchyEvent.HIERARCHY_CHANGED)
					&&	((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0)
					&&	AbstractWizardPanel.this.isShowing())
				{
					// We must wait until the GUI is visible to show first step
					// of wizard sequence, so that we are sure its enter() method
					// has been called already
					goToStep(0);
				}
            }
	    });
    }

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.support.AbstractPanel#initLayout()
	 */
	@Override final protected void initLayout()
    {
		_mainPane.initLayout(this);
    }

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.support.AbstractPanel#setupButtonsList(java.util.List, javax.swing.JButton, javax.swing.JButton)
	 */
	@Override final protected void setupButtonsList(
		List<JButton> buttons, JButton ok, JButton cancel)
    {
		// Create previous and next buttons
		JButton previous = createButton("previous", "previous");
		JButton next = createButton("next", "next");
		// Setup the full list
		buttons.add(previous);
		buttons.add(next);
		buttons.add(ok);
		buttons.add(cancel);
    }
	
	@Action(enabledProperty = NEXT_ENABLED) 
	final public <R, S> Task<R, S> next()
	{
		// First accept current pane
		JComponent current = _panes.get(_sequence.get(_current));
		if (current instanceof WizardStepPanel)
		{
			Task<R, S> task = ((WizardStepPanel) current).leave();
			if (task != null)
			{
				// Use TaskListener to go on after success
				task.addTaskListener(new TaskListener.Adapter<R, S>()
				{
					@Override public void succeeded(TaskEvent<R> e)
	                {
						goToStep(++_current);
	                }
				});
				return task;
			}
		}
		goToStep(++_current);
		return null;
	}
	
	private void goToStep(int index)
	{
		String step = _sequence.get(index);
		JComponent pane = _panes.get(step);
		boolean acceptEnabled = (index == _sequence.size() - 1);
		_controller.setNextEnabled(!acceptEnabled);
		_controller.setAcceptEnabled(acceptEnabled);
		if (pane instanceof WizardStepPanel)
		{
			WizardStepPanel stepPane = (WizardStepPanel) pane;
			stepPane.setController(_controller);
			stepPane.enter();
		}
		setPreviousEnabled(index > 0);
		_mainPane.showStep(step);
	}

	@Action(enabledProperty = PREVIOUS_ENABLED) 
	final public void previous()
	{
		if (_current > 0)
		{
			// Return to previous pane
			goToStep(--_current);
		}
	}

	@Override final public void reset()
    {
		_mainPane.reset();
		_panes.clear();
		_sequence.clear();
		_current = 0;
		setPreviousEnabled(false);
		setNextEnabled(false);
		setAcceptEnabled(false);
	    super.reset();
	    initWizard();
    }
	
	protected final WizardController getController()
    {
    	return _controller;
    }

	abstract protected void initWizard();

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.support.AbstractMultiPanel#getSubComponents()
	 */
	@Override final protected Iterable<JComponent> getSubComponents()
    {
	    return _panes.values();
    }

	public final boolean isNextEnabled()
    {
    	return _nextEnabled;
    }

	public final boolean isPreviousEnabled()
    {
    	return _previousEnabled;
    }

	private final void setPreviousEnabled(boolean previousEnabled)
    {
		boolean old = _previousEnabled;
		_previousEnabled = previousEnabled;
		firePropertyChange(PREVIOUS_ENABLED, old, _previousEnabled);
    }
	
	private final void setNextEnabled(boolean enabled)
    {
		boolean old = _nextEnabled;
    	_nextEnabled = enabled;
		firePropertyChange(NEXT_ENABLED, old, _nextEnabled);
    }

	private class WizardControllerImpl implements WizardController
	{
		public void addWizardPane(JComponent pane, boolean appendToSequence)
        {
			_mainPane.addStep(pane);
			_panes.put(pane.getName(), pane);
			if (appendToSequence)
			{
				_sequence.add(pane.getName());
			}
        }

		public void setAcceptEnabled(boolean enabled)
        {
			AbstractWizardPanel.this.setAcceptEnabled(enabled);
        }

		public void setNextEnabled(boolean enabled)
        {
			AbstractWizardPanel.this.setNextEnabled(enabled);
        }

		public void setNextStepsSequence(String... steps)
        {
			// First check that all steps match added components
			if (!_panes.keySet().containsAll(Arrays.asList(steps)))
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
			_sequence.addAll(Arrays.asList(steps));
        }

		public <U> U getContext(Class<U> clazz)
        {
	        return clazz.cast(_context.get(clazz));
        }

		public <U> void setContext(U context)
        {
			_context.put(context.getClass(), context);
        }
		
		private final Map<Class<?>, Object> _context = new HashMap<Class<?>, Object>();
	}
	
	private static final long serialVersionUID = 7779160067777339171L;

	static final private String PREVIOUS_ENABLED = "previousEnabled";
	static final private String NEXT_ENABLED = "nextEnabled";

	private final WizardPanelHelper _mainPane;
	private final WizardController _controller = new WizardControllerImpl();
	private final HashMap<String, JComponent> _panes =  new HashMap<String, JComponent>();
	private final List<String> _sequence = new ArrayList<String>();
	private int _current = 0;
	private boolean _previousEnabled;
	private boolean _nextEnabled;
}
