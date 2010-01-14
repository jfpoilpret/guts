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
