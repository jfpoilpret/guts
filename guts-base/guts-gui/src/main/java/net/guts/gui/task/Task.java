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
 * Background operations that you want under control of a {@link TasksGroup} must
 * implement the {@code Task<T>} interface.
 * <p/>
 * A {@code Task} is in charge of a processing operation that is too long to be
 * directly executed from the EDT. {@code TasksGroup} guarantees that its tasks
 * are executed outside the EDT <a href="#footnote">(*)</a>.
 * <p/>
 * {@code Task<T>} implementations should not call Swing component methods (besides
 * the ones explicitly documented as "thread-safe", like 
 * {@link javax.swing.JComponent#repaint()}).
 * <p/>
 * <a name="footnote">(*)</a> Actually, it is not absolutely 100% correct because you 
 * may always define an {@link java.util.concurrent.ExecutorService} that executes all
 * {@link Runnable}s in the EDT (by calling {@link javax.swing.SwingUtilities#invokeLater}).
 * But we don't see any reason to do so and that would be looking for trouble!
 * 
 * @param <T> the type of result returned by {@code this} task, which will be passed
 * to all properly registered {@link TaskListener}s
 *
 * @author Jean-Francois Poilpret
 */
public interface Task<T>
{
	/**
	 * This method shall perform the long operation of {@code this} task; it will be
	 * called from a background {@code Thread}.
	 * <p/>
	 * The operation can use {@code controller} to give feedback about its own progress
	 * to registered {@link TaskListener}s.
	 * <p/>
	 * If {@code this} task is cancellable, then it should, during its long processing,
	 * call {@code controller.isCancelled()} and terminate immediately if that method
	 * returns {@code true}.
	 * 
	 * @param controller a controller that can be used to give feedback about the current
	 * progress of {@code this} task
	 * @return the processing result of {@code this} task; it will be notified to
	 * {@link TaskListener#succeeded(TasksGroup, TaskInfo, Object)}.
	 * @throws Exception any exception can be thrown; if {@code execute} throws an
	 * exception, it will be notified to 
	 * {@link TaskListener#failed(TasksGroup, TaskInfo, Throwable)}.
	 */
	public T execute(FeedbackController controller) throws Exception;
}
