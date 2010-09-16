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

import javax.swing.JComponent;

import net.guts.gui.action.GutsAction;
import net.guts.gui.action.TaskAction;
import net.guts.gui.task.DelegatingTask;
import net.guts.gui.task.Task;
import net.guts.gui.task.TaskInfo;
import net.guts.gui.task.TasksGroup;
import net.guts.gui.task.blocker.InputBlockers;

/**
 * Abstract panel to be used in "wizard" dialogs.
 * <p/>
 * This class manages most repetitive stuff for you: creating 
 * OK/Cancel/Previous/Next buttons and associated Actions, inject resources 
 * into components from properties files...
 * <p/>
 * Typically, your panels would look like:
 * <pre>
 * public class MyPanel extends AbstractWizardPanel {
 *     static final private String ID = "my-panel-id";
 *     
 *     final private WizardPane1 _pane1 = new WizardPane1();
 *     final private WizardPane2 _pane2 = new WizardPane2();
 *     ...
 *     &#64;Inject private SomeService _service;
 *     ...
 *     
 *     public MyPanel() {
 *         setName(ID);
 *     }
 *     
 *     &#64;Override protected void initWizard() {
 *         getController().addWizardPane(_pane1, true);
 *         getController().addWizardPane(_pane2, true);
 *     }
 *     
 *     &#64;Override protected GutsAction getAcceptAction() {
 *         return _accept;
 *     }
 *     
 *     final private GutsAction _accept = new GutsAction() {
 *         &#64;Override protected void perform() {
 *             // Something to be done when user clicks OK
 *             ...
 *         }
 *     };
 * }
 * </pre>
 * In the snippet above, note the {@link #initWizard()} and {@link #getAcceptAction()} 
 * methods, further explained hereafter:
 * <p/>
 * {@code initWizard()} adds 2 wizard panes, {@code _pane1} and {@code _pane2} to 
 * the wizard dialog; the first added panel will be the first wizard pane to appear.
 * <p/>
 * {@code getAcceptAction()} must return the action that will be executed once the
 * user clicks the "Finish" button.
 * <p/>
 * {@code AbstractWizardPanel} automatically manages enabling of "Previous", "Next"
 * and "Finish" buttons in the following way:
 * <ul>
 * <li>"Previous" is enabled if there is at least one wizard pane before the current
 * pane; this behavior cannot be changed.</li>
 * <li>"Next" is enabled if there is at least one wizard pane after the current
 * pane.</li>
 * <li>"Finish" is enabled if there is no wizard pane after the current pane.</li>
 * </ul>
 * This default behavior matches the most common cases of wizard usage, but enabling
 * of "Next" and "Finish" can be directly set by {@link WizardStepPanel#enter}, 
 * through {@link WizardController#setNextEnabled(boolean)} and 
 * {@link WizardController#setAcceptEnabled(boolean)}.
 * <p/>
 * The following snippet shows an example of a wizard pane:
 * <pre>
 * class WizardPane1 extends JPanel implements WizardStepPanel {
 *     final private JTextField _txfFirstName = new JTextField(20);
 *     final private JTextField _txfLastName = new JTextField(20);
 *     private Contact _contact;
 * 
 *     public WizardPane1() {
 *         // Init components and set layout
 *         ...
 *     }
 *     
 *     &#64;Override public void enter(WizardController controller) {
 *         // Copy from domain to widgets
 *         _contact = controller.getContext(Contact.class);
 *         _txfFirstName.setText(_contact.getFirstName());
 *         _txfLastName.setText(_contact.getLastName());
 *     }
 *     
 *     &#64;Override public &lt;T&gt; Task&lt;T&gt; leave(WizardController controller) {
 *         // Copy from widgets to Domain
 *         _contact.setFirstName(_txfFirstName.getText());
 *         _contact.setLastName(_txfLastName.getText());
 *         return null;
 *     }
 * }
 * </pre>
 * <p/>
 * <b>Important!</b> Note that {@code AbstractWizardPanel} makes use of 
 * <a href="https://designgridlayout.dev.java.net/">DesignGridLayout</a>, hence
 * this dependency becomes mandatory if you use {@code AbstractWizardPanel}.
 *
 * @author Jean-Francois Poilpret
 */
abstract public class AbstractWizardPanel extends AbstractMultiPanel
{
	/**
	 * Constructs a new abstract wizard panel.
	 */
	protected AbstractWizardPanel()
    {
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
		_mainPane.initLayout(this);
    }

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.dialog.support.AbstractPanel#finishInitialization()
	 */
	@Override final protected void finishInitialization()
	{
		_mainPane.setName(getName());
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.dialog.support.AbstractPanel#setupActions(java.util.List)
	 */
	@Override final protected void setupActions(List<GutsAction> actions)
	{
		actions.add(_previous);
		actions.add(_next);
		actions.add(getAcceptAction());
		actions.add(getCancelAction());
	}

	final private GutsAction _next = new TaskAction("next")
	{
		@Override protected void perform()
		{
			submit(next(), InputBlockers.actionBlocker(this));
		}
	};
	
	@SuppressWarnings("unchecked") private Task<?> next()
	{
		// First accept current pane
		JComponent current = _panes.get(_sequence.get(_current));
		if (current instanceof WizardStepPanel)
		{
			Task<?> task = ((WizardStepPanel) current).leave(_controller);
			if (task != null)
			{
				return new DelegatingTask(task)
				{
					@Override public void succeeded(
						TasksGroup group, TaskInfo source, Object result)
					{
						goToStep(++_current);
					}
				};
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
			stepPane.enter(_controller);
		}
		setPreviousEnabled(index > 0);
		_mainPane.showStep(step);
	}

	final private GutsAction _previous = new GutsAction("previous")
	{
		@Override protected void perform()
		{
			previous();
		}
	};
	
	private void previous()
	{
		if (_current > 0)
		{
			// Return to previous pane
			goToStep(--_current);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.guts.gui.dialog.support.AbstractMultiPanel#reset()
	 */
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

	/**
	 * Get the wizard controller for {@code this} wizard dialog. This controller
	 * must be used to control the sequence of steps and wizard panes that make
	 * this wizard dialog.
	 */
	protected final WizardController getController()
    {
    	return _controller;
    }

	/**
	 * Called every time this wizard dialog is {@link #reset()}, this abstract 
	 * method must be implemented to initialize the dialog state and the path of
	 * wizard panes to be displayed, through the use of {@link #getController()}.
	 * <p/>
	 * This method must {@linkplain WizardController#addWizardPane(JComponent, boolean) add} 
	 * at least one wizard pane (the first to be displayed).
	 */
	abstract protected void initWizard();

	/*
	 * (non-Javadoc)
	 * @see net.sf.guice.gui.dialog.support.AbstractMultiPanel#getSubComponents()
	 */
	@Override final protected Iterable<JComponent> getSubComponents()
    {
	    return _panes.values();
    }

	private final void setAcceptEnabled(boolean enabled)
	{
		getAcceptAction().setEnabled(enabled);
	}
	
	private final void setPreviousEnabled(boolean enabled)
    {
		_previous.setEnabled(enabled);
    }
	
	private final void setNextEnabled(boolean enabled)
    {
		_next.setEnabled(enabled);
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

	private final WizardPanelHelper _mainPane = new WizardPanelHelper();
	private final WizardController _controller = new WizardControllerImpl();
	private final Map<String, JComponent> _panes =  new HashMap<String, JComponent>();
	private final List<String> _sequence = new ArrayList<String>();
	private int _current = 0;
}
