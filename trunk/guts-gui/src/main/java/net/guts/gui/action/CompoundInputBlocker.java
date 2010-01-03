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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CompoundInputBlocker implements InputBlocker
{
	static public CompoundInputBlocker combineBlockers(InputBlocker... blockers)
	{
		CompoundInputBlocker compoundBlocker = new CompoundInputBlocker();
		for (InputBlocker blocker: blockers)
		{
			compoundBlocker.appendBlocker(blocker);
		}
		return compoundBlocker;
	}

	private CompoundInputBlocker()
	{
	}
	
	public CompoundInputBlocker appendBlocker(InputBlocker blocker)
	{
		_blockers.add(blocker);
		return this;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.InputBlocker#block()
	 */
	@Override public void block()
	{
		for (InputBlocker blocker: _blockers)
		{
			blocker.block();
		}
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.InputBlocker#unblock()
	 */
	@Override public void unblock()
	{
		ListIterator<InputBlocker> i = _blockers.listIterator(_blockers.size());
		while (i.hasPrevious())
		{
			i.previous().unblock();
		}
	}

	final private List<InputBlocker> _blockers = new ArrayList<InputBlocker>();
}
