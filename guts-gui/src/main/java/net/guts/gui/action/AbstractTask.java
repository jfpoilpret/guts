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

import java.util.List;

public abstract class AbstractTask<T, V> implements Task<T, V>
{
	/* (non-Javadoc)
	 * @see net.guts.gui.action.Task#failed(java.lang.Throwable)
	 */
	@Override public void failed(Throwable cause)
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.Task#finished()
	 */
	@Override public void finished()
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.Task#process(java.util.List)
	 */
	@Override public void process(List<V> chunks)
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.Task#succeeded(java.lang.Object)
	 */
	@Override public void succeeded(T result)
	{
	}
}
