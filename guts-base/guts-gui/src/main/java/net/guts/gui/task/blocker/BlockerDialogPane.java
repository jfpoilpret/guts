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

import javax.swing.JComponent;

import net.guts.gui.task.TasksGroup;

import com.google.inject.ImplementedBy;

/**
 * This interface must be implemented by a panel you want to be used by 
 * {@link ModalDialogInputBlocker}. For your panel to be used, you also have
 * to add a binding to it in one of your Guice {@link com.google.inject.Module}s:
 * <p>
 * bind(BlockerDialogPane.class).to(MyBlockerDialogPane.class).in(Scopes.SINGLETON);
 * </p>
 * Note that your panel class can be bound as a singleton; every time before use,
 * its {@link #setTasksGroup(TasksGroup)} will be called for the current executing
 * {@code TasksGroup}.
 * <p/>
 * In general, you won't need to provide your own panel except if Guts-GUI default,
 * {@link DefaultBlockerDialogPane}, doesn't suit you.
 *
 * @author Jean-Francois Poilpret
 */
@ImplementedBy(DefaultBlockerDialogPane.class)
public interface BlockerDialogPane
{
	public JComponent getPane();
	public void setTasksGroup(TasksGroup tasks);
}
