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
 * Information about progress of a given {@link Task} as executed by a 
 * {@link TasksGroup}.
 * <p/>
 * This is used for task monitoring support, e.g. in a task progress dialog or in
 * a status bar.
 *
 * @author Jean-Francois Poilpret
 */
public interface TaskInfo
{
	/**
	 * Current state of the {@code Task} represented by {@code this}.
	 * 
	 * @return current task state
	 */
	public State state();
	
	/**
	 * Current progress (from 0 to 100) of the {@code Task} represented by 
	 * {@code this}.
	 * 
	 * @return current task progress
	 */
	public int progress();
	
	/**
	 * Latest feedback reported by the {@code Task} represented by {@code this}.
	 * 
	 * @return latest task feedback
	 */
	public String feedback();
	
	/**
	 * Possible state of a {@link Task} inside a {@link TasksGroup}.
	 */
	public enum State
	{
		/** The {@code Task} has not been started yet by its {@code TasksGroup}. */
		NOT_STARTED,
		
		/** 
		 * The {@code Task} has been started by its {@code TasksGroup} and is 
		 * running normally.
		 */
		RUNNING,
		
		/** The {@code Task} has terminated execution normally without any problem. */
		FINISHED,
		
		/**
		 * The {@code Task} has failed and hence was terminated; this can be due to
		 * being interrupted or by throwing any exception.
		 */
		FAILED,
		
		/**
		 * The {@code Task} was cancelled as a result of its {@code TasksGroup} 
		 * cancellation by an external call to {@link TasksGroup#cancel()}.
		 */
		CANCELLED
	}
}
