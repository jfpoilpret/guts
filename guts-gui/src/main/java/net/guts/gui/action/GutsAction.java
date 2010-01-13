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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.google.inject.Inject;

// CSOFF: AbstractClassNameCheck
abstract public class GutsAction
{
	protected GutsAction(String name)
	{
		_name = name;
	}

	final public Action action()
	{
		return _action;
	}
	
	final public String name()
	{
		return _name;
	}

	final public ActionEvent event()
	{
		return _event;
	}
	
	abstract protected void perform();

	@SuppressWarnings("serial") 
	private class InternalAction extends AbstractAction
	{
		@Override public void actionPerformed(ActionEvent event)
		{
			_event = event;
			perform();
		}
	}
	
	@Override final public boolean equals(Object that)
	{
		return this == that;
	}

	@Override final public int hashCode()
	{
		return System.identityHashCode(this);
	}
	
	@Inject void init()
	{
		_injectedAlready = true;
	}

	// Used by ActionRegistrationManager to prevent Guice injection occurring twice
	boolean isMarkedInjected()
	{
		return _injectedAlready;
	}
	
	final private String _name;
	final private Action _action = new InternalAction();
	private ActionEvent _event = null;
	
	private boolean _injectedAlready = false;
}
//CSON: AbstractClassNameCheck
