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

package net.guts.gui.task;

/**
 * Abstract {@link Task} that simply delegates its execution to another given 
 * task (called {@code delegate}).
 * <p/>
 * This can be useful when a method is handed a {@code Task} and must return 
 * another {@code Task} which execution must be monitored:
 * <pre>
 * public &lt;T&gt; Task&lt;T&gt; convert(Task&lt;T&gt; source)
 * {
 *     return new DelegatingTask&lt;T&gt;(source)
 *     {
 *         &#63;Override public void succeeded(TasksGroup group, TaskInfo source, T result)
 *         {
 *             // Do something special (in EDT) after source task has succeeded
 *         }
 *     };
 * }
 * </pre>
 * <p/>
 * Note that you don't need {@code DelegatingTask} in a piece of code that already 
 * creates its own {@link Task} subclass, it is preferable to directly use 
 * {@link AbstractTask} in this case.
 * 
 * @param <T> the type of result returned by {@code this} task, which will be passed
 * to all properly registered {@link TaskListener}s
 *
 * @author Jean-Francois Poilpret
 */
//CSOFF: AbstractClassNameCheck
public abstract class DelegatingTask<T> extends AbstractTask<T>
{
	protected DelegatingTask(Task<T> delegate)
	{
		_delegate = delegate;
	}

	/* (non-Javadoc)
	 * @see net.guts.gui.task.Task#execute(net.guts.gui.task.FeedbackController)
	 */
	@Override public T execute(FeedbackController controller) throws Exception
	{
		return _delegate.execute(controller);
	}

	private final Task<T> _delegate;
}
//CSON: AbstractClassNameCheck
