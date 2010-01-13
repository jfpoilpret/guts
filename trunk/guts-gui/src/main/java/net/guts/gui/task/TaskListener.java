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

// All methods are called in the EDT
public interface TaskListener<T>
{
	// Notifier callbacks on thread progress
	public void progress(TasksGroup group, Task<? extends T> source, int rate);
	public void feedback(TasksGroup group, Task<? extends T> source, String note);

	// End of the background thread, depending on status
	public void succeeded(TasksGroup group, Task<? extends T> source, T result);
	public void failed(TasksGroup group, Task<? extends T> source, Throwable cause);
	public void cancelled(TasksGroup group, Task<? extends T> source);
	public void interrupted(
		TasksGroup group, Task<? extends T> source, InterruptedException cause);
	
	// Always called in the end, whatever the status of the background thread
	public void finished(TasksGroup group, Task<? extends T> source);
}
