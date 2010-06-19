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

public class GutsActionDecorator extends GutsAction
{
	public GutsActionDecorator(String name, GutsAction target)
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
		action().setEnabled(_target.action().isEnabled());
		// Bind properties (including enabling) of both actions
		PropertyChangeListener listener = new PropertyChangeListener()
		{
			@Override public void propertyChange(PropertyChangeEvent evt)
			{
				Action action = (evt.getSource() == action() ? _target.action() : action());
				action.putValue(evt.getPropertyName(), evt.getNewValue());
			}
		};
		action().addPropertyChangeListener(listener);
		_target.action().addPropertyChangeListener(listener);
	}
	
	private void copyProperty(String name)
	{
		action().putValue(name, _target.action().getValue(name));
	}
	
	public GutsActionDecorator(GutsAction target)
	{
		this(target.name(), target);
	}
	
	protected void beforeTargetPerform()
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.GutsAction#perform()
	 */
	@Override final protected void perform()
	{
		beforeTargetPerform();
		_target.action().actionPerformed(event());
		afterTargetPerform();
	}

	protected void afterTargetPerform()
	{
	}

	final private GutsAction _target;
}
