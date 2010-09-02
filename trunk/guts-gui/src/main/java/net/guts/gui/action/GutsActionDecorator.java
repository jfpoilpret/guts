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

package net.guts.gui.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;

/**
 * This class wraps an existing {@link GutsAction} in order to either add 
 * specific behavior before or after the {@code target} action is performed, 
 * or change its name, or both.
 * <p/>
 * Both {@code target} and {@code this} actions share the same {@link Action} 
 * properties at all times, so that you can manipulate either, it will impact 
 * both {@code Action} instances.
 *
 * @author Jean-Francois Poilpret
 */
public class GutsActionDecorator extends GutsAction
{
	/**
	 * Create a new wrapper for the given {@code target}, while enforcing a new
	 * {@code name} for it.
	 */
	public GutsActionDecorator(String name, Action target)
	{
		super(name);
		_target = target;
		// Set all action properties from target action
		copyProperty(Action.ACCELERATOR_KEY);
		copyProperty(Action.ACTION_COMMAND_KEY);
		copyProperty(Action.NAME);
		copyProperty(Action.MNEMONIC_KEY);
		copyProperty(Action.DISPLAYED_MNEMONIC_INDEX_KEY);
		copyProperty(Action.LARGE_ICON_KEY);
		copyProperty(Action.LONG_DESCRIPTION);
		copyProperty(Action.SELECTED_KEY);
		copyProperty(Action.SHORT_DESCRIPTION);
		copyProperty(Action.SMALL_ICON);
		setEnabled(_target.isEnabled());
		// Bind properties (including enabling) of both actions
		PropertyChangeListener listener = new PropertyChangeListener()
		{
			@Override public void propertyChange(PropertyChangeEvent evt)
			{
				Action action = (evt.getSource() == this ? _target : GutsActionDecorator.this);
				action.putValue(evt.getPropertyName(), evt.getNewValue());
			}
		};
		addPropertyChangeListener(listener);
		_target.addPropertyChangeListener(listener);
	}

	/**
	 * Create a new wrapper for the given {@code target} but keeps its original
	 * name, as returned by {@code target.name()}.
	 * <p/>
	 * Note that, if {@code target} doesn't have a name yet (e.g. automatic naming
	 * has not occurred yet), then {@code this} action won't have a name either,
	 * and any later name change (through automatic naming) for any of these 
	 * actions will not impact the other action name.
	 */
	public GutsActionDecorator(GutsAction target)
	{
		this(target.name(), target);
	}
	
	private void copyProperty(String name)
	{
		putValue(name, _target.getValue(name));
	}

	/**
	 * Called just before calling {@code target} {@link #perform()}, this method
	 * can be overridden to perform any necessary preliminary work.
	 * Does nothing by default.
	 */
	protected void beforeTargetPerform()
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.GutsAction#perform()
	 */
	@Override final protected void perform()
	{
		beforeTargetPerform();
		_target.actionPerformed(event());
		afterTargetPerform();
	}

	/**
	 * Called just after calling {@code target} {@link #perform()}, this method
	 * can be overridden to perform any necessary post-performance work.
	 * Does nothing by default.
	 */
	protected void afterTargetPerform()
	{
	}

	final private Action _target;
}
