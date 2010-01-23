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
 * This interface defines the API of {@code InputBlocker}s, as called by 
 * a {@link TasksGroup}, around execution of its {@link net.guts.gui.task.Task}s.
 * <p/>
 * The contract is quite straightforward: {@code TasksGroup} guarantees that {@link #block}
 * is called, inside the EDT, immediately before the first {@code Task} of 
 * {@code TasksGroup} starts execution; it also guaranteed that {@link InputBlocker#unblock}
 * is called, inside the EDT, immediately after the last {@code Task}} of {@code TasksGroup}
 * has finished execution; note that {@link #unblock} will be called whatever the way all
 * {@code Task}s have terminated (successfully, with a failure, cancelled).
 * <p/>
 * Any {@code InputBlocker} instance passed to {@link net.guts.gui.task.TasksGroupFactory}
 * will be processed by {@code TasksGroup}, before actual use for user input blocking, 
 * as follows:
 * <ul>
 * <li>Any {@code @Inject}-marked methods and fields in that {@code InputBlocker} class
 * are injected by Guice (through {@link com.google.inject.Injector#injectMembers})</li>
 * <li>Any bean properties (with any setter, {@code public} or not) will be injected
 * with values extracted from the application resource bundles (as described in
 * {@link net.guts.gui.resource.ResourceInjector#injectInstance(Object)})</li>
 * </ul>
 *
 * @author Jean-Francois Poilpret
 */
public interface InputBlocker
{
	/**
	 * Called immediately before the first {@link net.guts.gui.task.Task} from {@code tasks}
	 * starts execution. It should block user input in any suitable way as defined for
	 * this concrete implementation. {@code block()} is called from the EDT.
	 * 
	 * @param tasks the {@code TasksGroup}, about to start execution, for which user input
	 * should be blocked
	 */
	public void block(TasksGroup tasks);

	/**
	 * Called immediately after the last {@link net.guts.gui.task.Task} from {@code tasks}
	 * ends execution. It should unblock user input by "reversing" the blocking way it used
	 * in {@link #block(TasksGroup)}. {@code unblock()} is called from the EDT.
	 * 
	 * @param tasks the {@code TasksGroup}, which tasks have just ended execution, for which 
	 * user input should be restored (unblocked)
	 */
	public void unblock(TasksGroup tasks);
}
