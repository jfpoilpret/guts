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

import java.lang.annotation.Annotation;

import net.guts.gui.action.blocker.InputBlocker;

public interface TaskService
{
	public <T, V> void addTaskListener(Task<T, V> task, TaskListener<T, V> listener);
	public <T, V> void removeTaskListener(Task<T, V> task, TaskListener<T, V> listener);
	public void addTaskListener(TaskListener<Object, Object> listener);
	public void removeTaskListener(TaskListener<Object, Object> listener);
	
	public <T, V> void execute(Task<T, V> task, InputBlocker blocker);
	public <T, V> void execute(
		Task<T, V> task, GutsAction source, Class<? extends Annotation> blocker);
}
