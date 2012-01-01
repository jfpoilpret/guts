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
 * Special {@link Task} that derives from {@link TaskAdapter}, hence allows
 * to combine a background operation with progress/state notifications called
 * in the EDT.
 * <p/>
 * {@link TasksGroup#add(Task)} performs special handling of {@code AbstractTask}
 * instances, ie it recognizes those instances as both a {@code Task<T>} and a
 * {@code TaskListener<T>} and handles it as such.
 * 
 * @param <T> the type of result returned by {@code this} task, which will be passed
 * to all properly registered {@link TaskListener}s
 *
 * @author Jean-Francois Poilpret
 */
public abstract class AbstractTask<T> extends TaskAdapter<T> implements Task<T>
{
}
