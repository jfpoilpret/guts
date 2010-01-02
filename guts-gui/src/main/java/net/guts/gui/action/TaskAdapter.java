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

//CSOFF: AbstractClassNameCheck
public abstract class TaskAdapter<T, V> implements TaskListener<T, V>
{
	/* (non-Javadoc)
	 * @see net.guts.gui.action.TaskListener#doInBackground(net.guts.gui.action.Task)
	 */
	@Override public void doInBackground(Task<?, ?> source)
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.TaskListener#failed(net.guts.gui.action.Task, java.lang.Throwable)
	 */
	@Override public void failed(Task<?, ?> source, Throwable cause)
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.TaskListener#finished(net.guts.gui.action.Task)
	 */
	@Override public void finished(Task<?, ?> source)
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.TaskListener#process(net.guts.gui.action.Task, java.util.List)
	 */
	@Override public void process(Task<?, ?> source, List<V> chunks)
	{
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.action.TaskListener#succeeded(net.guts.gui.action.Task, java.lang.Object)
	 */
	@Override public void succeeded(Task<?, ?> source, T result)
	{
	}
}
//CSON: AbstractClassNameCheck
