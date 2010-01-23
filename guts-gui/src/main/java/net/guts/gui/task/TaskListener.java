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
 * Listener for progress and status changes of {@link Task}s executed by a
 * {@link TasksGroup}.
 * <p/>
 * {@code TaskListener}s are added to a {@code TasksGroup} which is then
 * responsible to notify them of task events.
 * <p/>
 * A {@code TaskListener<T>} can either be added to a {@code TasksGroup} for a
 * given {@code Task<T>}, or a {@code TaskListener<Object>} can be added for
 * all {@code Task}s handled by a {@code TasksGroup}.
 * <p/>
 * {@code TasksGroup} ensures that all notifications are performed in the EDT,
 * hence {@code TaskListener} methods should be fast but can call methods on
 * any Swing component.
 * 
 * @param <T> The type returned by the {@code Task<T>} that this listener wants
 * to be notified about
 *
 * @author Jean-Francois Poilpret
 */
public interface TaskListener<T>
{
	/**
	 * Called when the {@code source} Task reports progress through 
	 * {@link FeedbackController#setProgress}.
	 * 
	 * @param group group to which {@code source} belongs
	 * @param source the task that has reported its progress
	 * @param rate the latest progress rate reported by {@code source}; rate is a
	 * progress percentage (from 0 to 100).
	 */
	public void progress(TasksGroup group, Task<? extends T> source, int rate);
	
	/**
	 * Called when the {@code source} Task reports feedback through 
	 * {@link FeedbackController#setProgress}.
	 * 
	 * @param group group to which {@code source} belongs
	 * @param source the task that has reported feedback
	 * @param note the latest feedback note reported by {@code source}
	 */
	public void feedback(TasksGroup group, Task<? extends T> source, String note);

	/**
	 * Called when {@code source} Task has terminated successfully.
	 * 
	 * @param group group to which {@code source} belongs
	 * @param source the task that has just terminated
	 * @param result the result returned by {@code source}
	 */
	public void succeeded(TasksGroup group, Task<? extends T> source, T result);
	
	/**
	 * Called when {@code source} Task has failed (ie has thrown an exception).
	 * 
	 * @param group group to which {@code source} belongs
	 * @param source the task that has just failed
	 * @param cause the exception thrown by {@code source}, causing it to fail
	 */
	public void failed(TasksGroup group, Task<? extends T> source, Throwable cause);
	
	/**
	 * Called when {@code source} Task has been cancelled.
	 * 
	 * @param group group to which {@code source} belongs
	 * @param source the task that has just failed
	 */
	public void cancelled(TasksGroup group, Task<? extends T> source);
	
	/**
	 * Called when {@code source} Task has been interrupted (ie has thrown an 
	 * {@link InterruptedException}).
	 * 
	 * @param group group to which {@code source} belongs
	 * @param source the task that has just been interrupted
	 * @param cause the exception thrown by {@code source}, causing its interruption
	 */
	public void interrupted(
		TasksGroup group, Task<? extends T> source, InterruptedException cause);

	/**
	 * Called when {@code source} Task has finished, whether it was successful or
	 * not. Always called after one of {@link #succeeded}, {@link #failed} or
	 * {@link #interrupted}.
	 * 
	 * @param group group to which {@code source} belongs
	 * @param source the task that has just finished
	 */
	public void finished(TasksGroup group, Task<? extends T> source);
}
