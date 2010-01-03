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

package net.guts.gui.action.blocker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.guts.gui.action.GutsAction;

public abstract class AbstractInputBlocker implements InputBlocker
{
	final protected Logger _logger = LoggerFactory.getLogger(getClass());

	protected AbstractInputBlocker(GutsAction source)
	{
		_source = source;
	}
	
	abstract protected void setBlocking(boolean block);

	/* (non-Javadoc)
	 * @see net.guts.gui.action.InputBlocker#block()
	 */
	@Override public void block()
	{
		if (_blocked)
		{
			_logger.error("block() called twice!");
		}
		setBlocking(true);
		_blocked = true;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.InputBlocker#unblock()
	 */
	@Override public void unblock()
	{
		if (!_blocked)
		{
			_logger.error("unblock() called before block()!");
		}
		setBlocking(false);
		_blocked = false;
	}
	
	private boolean _blocked = false;
	final protected GutsAction _source;
}
