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
 * Used by {@link Task#execute(FeedbackController)}, this interface allows to
 * give information on the current progress of a {@code Task}. it also enables
 * {@code Task}s to check if they were cancelled and should terminate immediately.
 *
 * @author Jean-Francois Poilpret
 */
public interface FeedbackController
{
	/**
	 * Called by a {@link Task}, from its {@link Task#execute(FeedbackController)} 
	 * method, to report its current progress rate.
	 * <p/>
	 * The new {@code current} progress rate will be notified to all {@link TaskListener}s
	 * of the calling {@code Task}.
	 * 
	 * @param current current task progress (must be between {@code 0} and {@code 100})
	 */
	public void setProgress(int current);
	
	/**
	 * Called by a {@link Task}, from its {@link Task#execute(FeedbackController)}
	 * method, to report some feedback on its current performance.
	 * <p/>
	 * The new {@code note} feedback will be notified to all {@link TaskListener}s of
	 * the calling {@code Task}.
	 * 
	 * @param note the feedback on current task process (or subtask) to be notified to
	 * {@code TaskListener}s
	 */
	public void setFeedback(String note);
	
	/**
	 * Called by a {@link Task}, from its {@link Task#execute(FeedbackController)}
	 * method, to check if the {@link TasksGroup} it belongs to has been cancelled,
	 * in which case it should terminate right away.
	 * 
	 * @return {@code true} if the calling {@code Task} should stop its current 
	 * processing and return immediately
	 */
	public boolean isCancelled();
}
