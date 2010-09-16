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

package net.guts.gui.task.blocker;

import net.guts.gui.task.TasksGroup;

/**
 * {@link InputBlocker} implementation that combines the behavior of several other
 * {@code InputBlocker} implementations.
 *
 * @author Jean-Francois Poilpret
 */
public class CompoundInputBlocker implements InputBlocker
{
	/**
	 * Create a new {@code CompoundInputBlocker} that delegates its blocking 
	 * behavior to {@code blockers}.
	 * <p/>
	 * The order of {@code blockers} is important:
	 * <ul>
	 * <li>{@code block()} will call {@code block()} of {@code blockers} in the 
	 * normal order</li>
	 * <li>{@code unblock()} will call {@code unblock()} of {@code blockers} in the 
	 * reverse order</li>
	 * </ul>
	 * 
	 * @param blockers blockers to which {@link #block} and {@link #unblock} will
	 * delegate blocking behavior
	 */
	public CompoundInputBlocker(InputBlocker... blockers)
	{
		_blockers = blockers;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.task.blocker.InputBlocker#block(net.guts.gui.task.TasksGroup)
	 */
	@Override public void block(TasksGroup tasks)
	{
		for (int i = 0; i < _blockers.length; i++)
		{
			_blockers[i].block(tasks);
		}
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.task.blocker.InputBlocker#unblock(net.guts.gui.task.TasksGroup)
	 */
	@Override public void unblock(TasksGroup tasks)
	{
		for (int i = _blockers.length - 1; i >= 0; i--)
		{
			_blockers[i].unblock(tasks);
		}
	}

	final private InputBlocker[] _blockers;
}
